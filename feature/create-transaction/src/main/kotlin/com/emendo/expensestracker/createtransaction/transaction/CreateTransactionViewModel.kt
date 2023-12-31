package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.common.ext.getNextItem
import com.emendo.expensestracker.core.app.common.ext.stateInEagerlyList
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.resourceValueOf
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.di.DecimalSeparator
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.labelResId
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import com.emendo.expensestracker.core.domain.account.GetLastTransferAccountOrRandomUseCase
import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.domain.currency.GetUsedCurrenciesUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorBottomSheetState
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorKeyboardActions
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
  getUsedCurrenciesUseCase: GetUsedCurrenciesUseCase,
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
  private val currencyCacheManager: CurrencyCacheManager,
  @DecimalSeparator private val decimalSeparator: String,
  private val createTransactionRepository: CreateTransactionRepository,
  private val transactionRepository: TransactionRepository,
  private val calculatorFormatter: CalculatorFormatter,
  private val appNavigationEventBus: AppNavigationEventBus,
  private val getLastTransferAccountOrRandomUseCase: GetLastTransferAccountOrRandomUseCase,
  private val convertCurrencyUseCase: ConvertCurrencyUseCase,
) : ViewModel(), ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate(), CalculatorKeyboardActions {

  private val _uiState: MutableStateFlow<CreateTransactionUiState> =
    MutableStateFlow(getDefaultCreateTransactionUiState())
  internal val uiState: StateFlow<CreateTransactionUiState> = combine(
    _uiState,
    createTransactionRepository.getTarget(),
    createTransactionRepository.getSource(),
    transform = ::combineCreateTransactionUiState,
  )
    .stateInWhileSubscribed(viewModelScope, getDefaultCreateTransactionUiState())

  private val _calculatorBottomSheetState: MutableStateFlow<CalculatorBottomSheetState> =
    MutableStateFlow(getInitialCalculatorState())

  private val _calculatorCurrencyState: MutableStateFlow<CurrencyModel?> =
    MutableStateFlow(createTransactionRepository.getSourceSnapshot()?.currency)
  private val sourceCurrency: Flow<CurrencyModel?> =
    sourceCurrencyFlow(createTransactionRepository, ::shouldKeepUserSelectedCurrency)

  private val currencyState: Flow<CurrencyModel?> = merge(_calculatorCurrencyState, sourceCurrency)
    .onEach { currency ->
      if (currency != null) {
        updateAmountCurrency(currency)
      }
    }

  private val calculatorState: StateFlow<CalculatorBottomSheetState> = combine(
    _calculatorBottomSheetState,
    numericKeyboardCommander.equalButtonState,
    currencyState
  ) { state, equalButtonState, currency ->
    state.copy(
      equalButtonState = equalButtonState,
      currency = currency?.currencySymbolOrCode,
    )
  }
    .stateInWhileSubscribed(viewModelScope, getInitialCalculatorState())

  private val usedCurrencies: StateFlow<List<CurrencyModel>> =
    getUsedCurrenciesUseCase()
      .stateInEagerlyList(viewModelScope)

  private val _bottomSheetState: MutableStateFlow<CreateTransactionBottomSheetState> by lazy {
    MutableStateFlow(
      CreateTransactionBottomSheetState(
        data = getCalculatorBottomSheetData(),
        show = createTransactionRepository.isBottomSheetVisibleOnInit(),
      )
    )
  }

  internal val bottomSheetState: StateFlow<CreateTransactionBottomSheetState> by lazy { _bottomSheetState.asStateFlow() }

  private var createTransactionJob: Job? = null
  private var shouldClearTarget: Boolean = true
  private var hasSelectedCustomCurrency = false

  init {
    numericKeyboardCommander.setCallbacks(
      doneClick = ::doneClick,
      onMathDone = ::updateAmountText,
      valueChanged = { text, _ ->
        updateAmountText(numericKeyboardCommander.getMathResult())
        updateCalculatorEditableHint(text)
        false
      }
    )
  }

  @ExperimentalMaterial3Api
  override fun confirmValueChange(sheetValue: SheetValue): Boolean {
    numericKeyboardCommander.doMath()
    if (sheetValue == SheetValue.Hidden) {
      _uiState.update { state ->
        state.copy(
          sourceAmountFocused = false,
          transferTargetAmountFocused = false,
        )
      }
    }
    return super.confirmValueChange(sheetValue)
  }

  fun changeTransactionType(type: TransactionType) {
    _uiState.update { state ->
      state.copy(
        screenData = state.screenData.copy(transactionType = type),
        sourceAmountFocused = true,
        transferTargetAmountFocused = false,
        isCustomTransferAmount = false,
      )
    }
    _calculatorBottomSheetState.update { calculatorState ->
      calculatorState.copy(transactionTypeLabelResId = type.labelResId)
    }
    numericKeyboardCommander.doMath()

    if (type != TransactionType.TRANSFER) {
      createTransactionRepository.setTarget(createTransactionRepository.getDefaultTarget(type))
      return
    }

    viewModelScope.launch {
      val target = getLastTransferAccountOrRandomUseCase(createTransactionRepository.getSourceSnapshot()?.id)
      createTransactionRepository.setTarget(target)

      if (target == null) {
        return@launch
      }

      val source = createTransactionRepository.getSourceSnapshot() ?: return@launch
      val transferReceivedAmount = getConvertedFormattedValue(
        value = uiState.value.amount.value,
        fromCurrency = source.currency,
        toCurrency = target.currency,
      )

      _uiState.update { state ->
        state.copy(transferReceivedAmount = transferReceivedAmount)
      }
    }
  }

  fun updateNoteText(newNote: String) {
    _uiState.update { it.copy(note = newNote) }
  }

  override fun changeTransactionType() {
    if (uiState.value.screenData.transactionType == TransactionType.EXPENSE) {
      changeTransactionType(TransactionType.INCOME)
      return
    }

    changeTransactionType(TransactionType.EXPENSE)
  }

  override fun onCurrencyClick() {
    val currencies = usedCurrencies.value

    if (currencies.isEmpty()) {
      return
    }

    hasSelectedCustomCurrency = true
    val newCurrency = getNextCurrency(currencies)
    _calculatorCurrencyState.update { newCurrency }
  }

  private fun updateAmountCurrency(newCurrency: CurrencyModel) {
    _uiState.update { state ->
      if (state.transferTargetAmountFocused) {
        val transferReceivedAmount = state.transferReceivedAmount
        if (transferReceivedAmount != null) {
          return@update state.copy(
            transferReceivedAmount = amountFormatter.replaceCurrency(
              amount = state.transferReceivedAmount,
              newCurrencyModel = newCurrency,
            )
          )
        } else {
          return@update state
        }
      }

      state.copy(
        amount = amountFormatter.replaceCurrency(
          amount = state.amount,
          newCurrencyModel = newCurrency,
        )
      )
    }
  }

  fun showCalculatorBottomSheet(sourceTrigger: Boolean = true) {
    numericKeyboardCommander.doMath()
    _uiState.update { state ->
      if (sourceTrigger) {
        state.copy(
          sourceAmountFocused = true,
          transferTargetAmountFocused = false,
        )
      } else {
        state.copy(
          sourceAmountFocused = false,
          transferTargetAmountFocused = true,
        )
      }
    }
    val amount = if (sourceTrigger) {
      uiState.value.amount
    } else {
      uiState.value.transferReceivedAmount
    }

    if (amount != null) {
      val initialValue = calculatorFormatter.formatFinalWithMax2Precision(amount.value)
      _calculatorCurrencyState.update { amount.currency }
      numericKeyboardCommander.setInitialValue(initialValue)
    }
    _bottomSheetState.update { it.copy(show = triggered) }
  }

  private fun getCalculatorBottomSheetData(): CalculatorBottomSheetData =
    CalculatorBottomSheetData(
      state = calculatorState,
      actions = this,
      numericKeyboardActions = numericKeyboardCommander,
      decimalSeparator = decimalSeparator,
    )

  fun saveTransaction() {
    val source = createTransactionRepository.getSourceSnapshot()

    if (source == null) {
      _uiState.updateScreenData { screenData ->
        screenData.copy(sourceError = triggered)
      }
      return
    }

    if (numericKeyboardCommander.currentValue == DEFAULT_AMOUNT_VALUE) {
      _uiState.updateScreenData { screenData ->
        screenData.copy(amountError = triggered)
      }
      return
    }

    val transactionType = checkNotNull(uiState.value).screenData.transactionType
    val target = createTransactionRepository.getTargetSnapshot() ?: getTargetDefaultValue(transactionType)
    createTransaction(source, target)
  }

  fun openAccountListScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectAccount())
  }

  private fun createTransaction(source: TransactionSource, target: TransactionTarget) {
    if (createTransactionJob != null) {
      return
    }

    createTransactionJob = viewModelScope.launch {
      transactionRepository.createTransaction(
        source = source,
        target = target,
        amount = uiState.value.amount,
        transferReceivedAmount = uiState.value.transferReceivedAmount,
        note = uiState.value.note,
      )

      numericKeyboardCommander.clear()
      hideCalculatorBottomSheet()
    }.apply {
      invokeOnCompletion {
        // Todo add loading state
        createTransactionJob = null
        navigateUp()
      }
    }
  }

  private fun updateCalculatorEditableHint(text: String) {
    _uiState.update { state ->
      if (state.transferTargetAmountFocused) {
        state.copy(transferReceivedCalculatorHint = text)
      } else {
        state.copy(amountCalculatorHint = text)
      }
    }
  }

  fun hideCalculatorBottomSheet() {
    _bottomSheetState.update { sheetState ->
      sheetState.copy(hide = triggered)
    }
    _uiState.update { state ->
      state.copy(sourceAmountFocused = false)
    }
  }

  private fun doneClick(): Boolean {
    hideCalculatorBottomSheet()
    return false
  }

  private fun updateAmountText(amount: String): Boolean =
    updateAmountText(calculatorFormatter.toBigDecimal(amount))

  private fun updateAmountText(amount: BigDecimal): Boolean {
    _uiState.update { state ->
      val selectedCurrency = getSelectedCurrency(state)
      val formattedAmount = amountFormatter.format(amount, selectedCurrency)

      if (state.screenData.transactionType != TransactionType.TRANSFER) {
        return@update state.copy(amount = formattedAmount)
      }

      // Transfer Target amount to be changed
      if (state.transferTargetAmountFocused) {
        return@update state.copy(
          transferReceivedAmount = formattedAmount,
          isCustomTransferAmount = state.newCustomTransferAmountValue(formattedAmount),
        )
      }

      // Transfer Source amount to be changed
      return@update state.copy(
        amount = formattedAmount,
        transferReceivedAmount = getTransferReceivedAmount(state, amount),
      )
    }

    return false
  }

  fun selectTransferTargetAccount() {
    appNavigationEventBus.navigate(
      AppNavigationEvent.SelectAccount(isTransferTargetSelect = true)
    )
  }

  fun duplicateTransaction() {
    val payload = createTransactionRepository.getTransactionPayload() ?: return
    shouldClearTarget = false
    appNavigationEventBus.navigate(
      AppNavigationEvent.CreateTransaction(
        source = createTransactionRepository.getSourceSnapshot(),
        target = createTransactionRepository.getTargetSnapshot(),
        payload = payload.copy(transactionId = null),
        shouldNavigateUp = true,
      )
    )
  }

  fun showConfirmDeleteTransactionBottomSheet() {
    showModalBottomSheet(
      GeneralBottomSheetData
        .Builder(Action.DangerAction(resourceValueOf(R.string.delete), ::deleteTransaction))
        .title(resourceValueOf(R.string.transaction_detail_dialog_delete_confirm_title))
        .negativeAction(resourceValueOf(R.string.cancel), ::hideModalBottomSheet)
        .build()
    )
  }

  private fun deleteTransaction() {
    val id = createTransactionRepository.getTransactionPayload()?.transactionId ?: return
    viewModelScope.launch {
      transactionRepository.deleteTransaction(id)
    }
    navigateUp()
  }

  fun consumeFieldError(field: FieldWithError) {
    _uiState.updateScreenData { state ->
      when (field) {
        is FieldWithError.Amount -> state.copy(amountError = consumed)
        is FieldWithError.Source -> state.copy(sourceError = consumed)
      }
    }
  }

  fun consumeCloseEvent() {
    _uiState.updateScreenData { state ->
      state.copy(navigateUp = triggered)
    }
  }

  fun consumeShowCalculatorBottomSheet() {
    _bottomSheetState.update { it.copy(show = consumed) }
  }

  fun consumeHideCalculatorBottomSheet() {
    _bottomSheetState.update { it.copy(hide = consumed) }
  }

  /**
   * Converts [sourceAmount] if the user didn't put any custom value into Transfer target amount
   *
   * @param state - screen ui state
   * @param sourceAmount - source amount [Amount]
   * @return if user put any custom value into Transfer target amount - Transfer target amount, otherwise - converted [sourceAmount]
   */
  private fun getTransferReceivedAmount(
    state: CreateTransactionUiState,
    sourceAmount: BigDecimal,
  ): Amount? =
    if (state.isCustomTransferAmount) state.transferReceivedAmount else getConvertedTransferAmount(sourceAmount)

  private fun getSelectedCurrency(state: CreateTransactionUiState): CurrencyModel {
    val screenData = state.screenData
    val currency: CurrencyModel = if (screenData.transactionType == TransactionType.TRANSFER) {
      when {
        state.sourceAmountFocused -> state.amount.currency
        state.transferTargetAmountFocused -> checkNotNull(state.transferReceivedAmount?.currency) { "Transfer target amount must not be focused with null Amount" }
        else -> throw IllegalStateException("There's no focused Amount input. We can't get the currency")
      }
    } else {
      state.amount.currency
    }
    return currency
  }

  private fun getConvertedTransferAmount(amount: BigDecimal): Amount? {
    val target: AccountModel = createTransactionRepository.getTargetSnapshot() as? AccountModel ?: return null
    val source = createTransactionRepository.getSourceSnapshot() ?: return null

    return getConvertedFormattedValue(
      value = amount,
      fromCurrency = source.currency,
      toCurrency = target.currency,
    )
  }

  private fun combineCreateTransactionUiState(
    uiState: CreateTransactionUiState,
    target: TransactionTarget?,
    source: TransactionSource?,
  ): CreateTransactionUiState {
    val screenData = uiState.screenData

    return uiState.copy(
      target = target.orDefault(screenData.transactionType),
      source = source?.toTransactionItemModel(),
    )
  }

  private fun getDefaultCreateTransactionUiState(): CreateTransactionUiState {
    val payload = createTransactionRepository.getTransactionPayload()
    payload?.transactionAmount?.let { transactionValue ->
      numericKeyboardCommander.setInitialValue(calculatorFormatter.formatFinalWithPrecision(transactionValue.value))
    }

    val source = createTransactionRepository.getSourceSnapshot()
    return CreateTransactionUiState(
      amount = payload?.transactionAmount ?: getDefaultAmount(source?.currency),
      screenData = CreateTransactionScreenData(transactionType = getTransactionType(payload)),
      target = createTransactionRepository.getTargetSnapshot().orDefault(TransactionType.DEFAULT),
      source = source?.toTransactionItemModel(),
      note = payload?.note,
      sourceAmountFocused = payload == null,
      transferReceivedAmount = payload?.transferReceivedAmount,
    )
  }

  private fun getDefaultAmount(currencyModel: CurrencyModel?): Amount =
    amountFormatter.format(DEFAULT_AMOUNT_VALUE, currencyModel ?: currencyCacheManager.getGeneralCurrencySnapshot())

  private fun TransactionTarget?.orDefault(transactionType: TransactionType): TransactionItemModel {
    val target = this ?: getTargetDefaultValue(transactionType)
    return target.toTransactionItemModel()
  }

  private fun getTargetDefaultValue(transactionType: TransactionType): TransactionTarget =
    createTransactionRepository.getDefaultTarget(transactionType)

  private fun getInitialCalculatorState(): CalculatorBottomSheetState =
    CalculatorBottomSheetState.initial(
      transactionTypeLabelResId = TransactionType.DEFAULT.labelResId,
      numericKeyboardActions = numericKeyboardCommander,
    )

  private fun CalculatorBottomSheetState.getSelectedCurrencyIndex(): Int =
    usedCurrencies.value.indexOfFirst { it.currencySymbolOrCode == currency }

  private fun getConvertedFormattedValue(
    value: BigDecimal,
    fromCurrency: CurrencyModel,
    toCurrency: CurrencyModel,
  ): Amount {
    val convertedValue = convertCurrencyUseCase(
      value = value,
      fromCurrencyCode = fromCurrency.currencyCode,
      toCurrencyCode = toCurrency.currencyCode,
    )
    return amountFormatter.format(convertedValue, toCurrency)
  }

  /**
   * Checks if the user really changed Amount. Prevents state change on just focusing transfer target amount
   */
  private fun CreateTransactionUiState.newCustomTransferAmountValue(amount: Amount): Boolean =
    if (isCustomTransferAmount) {
      true
    } else {
      transferReceivedAmount != amount
    }

  override fun onCleared() {
    super.onCleared()
    createTransactionRepository.clear(shouldClearTarget)
  }

  private fun shouldKeepUserSelectedCurrency(): Boolean {
    if (uiState.value.amount.value == DEFAULT_AMOUNT_VALUE) {
      return false
    }
    return hasSelectedCustomCurrency
  }

  private fun getNextCurrency(currencies: List<CurrencyModel>): CurrencyModel {
    val selectedCurrencyIndex = calculatorState.value.getSelectedCurrencyIndex()
    return currencies.getNextItem(selectedCurrencyIndex)
  }

  companion object {
    private val DEFAULT_AMOUNT_VALUE = BigDecimal.ZERO
  }
}

private fun CreateTransactionRepository.isBottomSheetVisibleOnInit(): StateEvent =
  if (getTransactionPayload() == null) triggered else consumed

private fun sourceCurrencyFlow(
  createTransactionRepository: CreateTransactionRepository,
  shouldKeepUserSelectedCurrency: () -> Boolean,
): Flow<CurrencyModel?> = createTransactionRepository
  .getSource()
  .transform { source ->
    if (!shouldKeepUserSelectedCurrency()) {
      emit(source?.currency)
    }
  }
  .distinctUntilChanged()