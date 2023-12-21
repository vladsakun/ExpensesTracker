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
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_TRANSFER_TRANSACTION
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
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.DEFAULT
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.labelResId
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.toTransactionType
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import com.emendo.expensestracker.core.domain.account.GetLastTransferAccountOrRandomUseCase
import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.domain.currency.GetUsedCurrenciesUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.exception.CurrencyRateNotFoundException
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorBottomSheetState
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorKeyboardActions
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel(), BottomSheetStateManager by BottomSheetStateManagerDelegate(), CalculatorKeyboardActions {

  private val _uiState: MutableStateFlow<CreateTransactionUiState> =
    MutableStateFlow(getDefaultCreateTransactionUiState())

  private val source: Flow<TransactionSource?> by lazy(LazyThreadSafetyMode.NONE) { createTransactionRepository.getSource() }

  val uiState: StateFlow<CreateTransactionUiState> = combine(
    _uiState,
    createTransactionRepository.getTarget(),
    source,
    transform = ::combineCreateTransactionUiState,
  )
    .stateInWhileSubscribed(viewModelScope, getDefaultCreateTransactionUiState())

  private val _calculatorBottomSheetState: MutableStateFlow<CalculatorBottomSheetState> =
    MutableStateFlow(getInitialCalculatorState())

  private val _calculatorCurrencyState: MutableStateFlow<CurrencyModel?> =
    MutableStateFlow(createTransactionRepository.getSourceSnapshot()?.currency)
  private val sourceCurrency: Flow<CurrencyModel?> = createTransactionRepository
    .getSource()
    .map { it?.currency }
    .distinctUntilChanged()

  private val superCurrencyState: Flow<CurrencyModel?> = merge(_calculatorCurrencyState, sourceCurrency)

  private val calculatorState: StateFlow<CalculatorBottomSheetState> = combine(
    _calculatorBottomSheetState,
    numericKeyboardCommander.calculatorTextState,
    numericKeyboardCommander.equalButtonState,
    superCurrencyState
  ) { state, text, equalButtonState, currency ->
    state.copy(
      text = text,
      equalButtonState = equalButtonState,
      currency = currency?.currencySymbolOrCode,
    )
  }
    .stateInWhileSubscribed(viewModelScope, getInitialCalculatorState())

  private val usedCurrencies: StateFlow<List<CurrencyModel>> =
    getUsedCurrenciesUseCase()
      .stateInEagerlyList(viewModelScope)

  private val selectedCurrencyModel: CurrencyModel
    get() = usedCurrencies.value.getOrNull(calculatorState.value.getSelectedCurrencyIndex())
      ?: currencyCacheManager.getGeneralCurrencySnapshot()

  private var createTransactionJob: Job? = null
  private var shouldClearTarget: Boolean = true

  init {
    numericKeyboardCommander.setCallbacks(
      doneClick = ::doneClick,
      onMathDone = ::updateAmountText
    )
    if (createTransactionRepository.getTransactionPayload() == null) {
      // Todo uncomment     showCalculatorBottomSheet()
    }
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
        sourceAmountFocused = false,
        transferTargetAmountFocused = false,
        isCustomTransferAmount = false,
      )
    }
    _calculatorBottomSheetState.update { calculatorState ->
      calculatorState.copy(transactionTypeLabelResId = type.labelResId)
    }

    if (type == TransactionType.TRANSFER) {
      viewModelScope.launch {
        val target = getLastTransferAccountOrRandomUseCase(createTransactionRepository.getSourceSnapshot()?.id)
        createTransactionRepository.setTarget(target)

        if (target == null) {
          return@launch
        }

        val source = createTransactionRepository.getSourceSnapshot() ?: return@launch
        val convertedValue = getConvertedTargetTransferValue(target, source, numericKeyboardCommander.currentValue)
        val toCurrency = target.currency

        _uiState.update { state ->
          state.copy(transferReceivedAmount = amountFormatter.format(convertedValue, toCurrency))
        }
      }
      return
    }

    createTransactionRepository.setTarget(createTransactionRepository.getDefaultTarget(type))
  }

  private fun getConvertedTargetTransferValue(
    target: AccountModel,
    source: TransactionSource,
    value: BigDecimal,
  ): BigDecimal {
    val toCurrency = target.currency
    val fromCurrency = source.currency
    return convertCurrencyUseCase.invoke(
      value = value,
      fromCurrencyCode = fromCurrency.currencyCode,
      toCurrencyCode = toCurrency.currencyCode,
    )
  }

  override fun changeTransactionType() {
    val transactionType = uiState.value.screenData.transactionType

    if (transactionType == TransactionType.EXPENSE) {
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

    val selectedCurrencyIndex = calculatorState.value.getSelectedCurrencyIndex()
    val nextCurrency = currencies.getNextItem(selectedCurrencyIndex)
    _uiState.updateScreenData { screenData ->
      screenData.copy(
        amount = amountFormatter.replaceCurrency(
          amount = screenData.amount,
          newCurrencyModel = nextCurrency,
        )
      )
    }
    _calculatorCurrencyState.update { nextCurrency }
  }

  fun showCalculatorBottomSheet(sourceTrigger: Boolean = true) {
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
      uiState.value.screenData.amount
    } else {
      uiState.value.transferReceivedAmount
    }

    if (amount != null) {
      val initialValue = calculatorFormatter.formatFinalWithMax2Precision(amount.value)
      _calculatorCurrencyState.update { amount.currency }
      _calculatorBottomSheetState.update { state ->
        state.copy(text = initialValue)
      }
      numericKeyboardCommander.setInitialValue(initialValue)
    }
    showBottomSheet(
      CalculatorBottomSheetData(
        state = calculatorState,
        actions = this,
        numericKeyboardActions = numericKeyboardCommander,
        decimalSeparator = decimalSeparator,
      )
    )
  }

  fun saveTransaction() {
    val source = createTransactionRepository.getSourceSnapshot()

    _uiState.updateScreenData { screenData ->
      screenData.copy(sourceError = triggered)
    }
    return

    if (source == null) {
      _uiState.updateScreenData { screenData ->
        screenData.copy(sourceError = triggered)
      }
      return
    }

    if (numericKeyboardCommander.currentValue == BigDecimal.ZERO) {
      _uiState.updateScreenData { screenData ->
        screenData.copy(amountError = triggered)
      }
      return
    }

    val transactionType = checkNotNull(uiState.value).screenData.transactionType
    val target = createTransactionRepository.getTargetSnapshot() ?: getTargetDefaultValue(transactionType)
    createTransaction(source, target)
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

  fun openAccountListScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectAccount())
  }

  private fun createTransaction(source: TransactionSource, target: TransactionTarget): Boolean {
    if (createTransactionJob != null) {
      return false
    }

    createTransactionJob = viewModelScope.launch {
      transactionRepository.createTransaction(
        source = source,
        target = target,
        amount = numericKeyboardCommander.currentValue,
        note = uiState.value.note,
      )

      numericKeyboardCommander.clear()
      hideBottomSheet()
    }.apply {
      invokeOnCompletion {
        // Todo add loading state
        createTransactionJob = null
        navigateUp()
      }
    }

    return false
  }

  private fun doneClick(): Boolean {
    hideBottomSheet()
    return false
  }

  private fun updateAmountText(amount: String): Boolean =
    updateAmountText(calculatorFormatter.toBigDecimal(amount))

  private fun updateAmountText(amount: BigDecimal): Boolean {
    _uiState.update { state ->
      val formattedAmount = amountFormatter.format(
        amount = amount,
        currency = selectedCurrencyModel,
      )
      if (state.screenData.transactionType == TransactionType.TRANSFER) {
        if (state.transferTargetAmountFocused) {
          return@update state.copy(
            transferReceivedAmount = formattedAmount,
            isCustomTransferAmount = true,
          )
        }

        if (!state.isCustomTransferAmount) {
          val target: AccountModel =
            (createTransactionRepository.getTargetSnapshot() as? AccountModel) ?: return@update state
          val source = createTransactionRepository.getSourceSnapshot() ?: return@update state
          val toCurrency = target.currency

          val fromCurrency = source.currency
          val convertedValue = try {
            convertCurrencyUseCase.invoke(
              value = amount,
              fromCurrencyCode = fromCurrency.currencyCode,
              toCurrencyCode = toCurrency.currencyCode,
            )
          } catch (e: CurrencyRateNotFoundException) {
            amount
          }

          val transferReceivedAmount = amountFormatter.format(convertedValue, toCurrency)
          return@update state.copy(
            screenData = state.screenData.copy(amount = formattedAmount),
            transferReceivedAmount = transferReceivedAmount,
          )
        }
      }

      state.copy(
        screenData = state.screenData.copy(
          amount = formattedAmount
        )
      )
    }

    return false
  }

  private fun combineCreateTransactionUiState(
    uiState: CreateTransactionUiState,
    target: TransactionTarget?,
    source: TransactionSource?,
  ): CreateTransactionUiState {
    return uiState.copy(
      target = target.orDefault(uiState.screenData.transactionType),
      source = source?.toTransactionItemModel()
    )
  }

  private fun getDefaultCreateTransactionUiState(): CreateTransactionUiState {
    val payload = createTransactionRepository.getTransactionPayload()
    payload?.transactionAmount?.let { transactionValue ->
      numericKeyboardCommander.setInitialValue(calculatorFormatter.formatFinalWithPrecision(transactionValue.value))
    }

    return CreateTransactionUiState(
      screenData = CreateTransactionScreenData.default(
        amount = payload?.transactionAmount ?: getZeroFormattedAmount(),
        transactionType = if (IS_DEBUG_TRANSFER_TRANSACTION) TransactionType.TRANSFER else payload?.transactionType?.toTransactionType()
          ?: DEFAULT
      ),
      target = createTransactionRepository.getTargetSnapshot().orDefault(TransactionType.DEFAULT),
      source = createTransactionRepository.getSourceSnapshot()?.toTransactionItemModel(),
      note = payload?.note,
    )
  }

  private fun getZeroFormattedAmount(): Amount =
    amountFormatter.format(
      BigDecimal.ZERO,
      currencyCacheManager.getGeneralCurrencySnapshot()
    ) // Todo check currency. maybe source

  private fun TransactionTarget?.orDefault(transactionType: TransactionType): TransactionItemModel {
    val target = this ?: getTargetDefaultValue(transactionType)
    return target.toTransactionItemModel()
  }

  private fun getTargetDefaultValue(transactionType: TransactionType): TransactionTarget =
    createTransactionRepository.getDefaultTarget(transactionType)

  private fun getInitialCalculatorState() =
    CalculatorBottomSheetState.initial(
      decimalSeparator = decimalSeparator,
      transactionTypeLabelResId = TransactionType.DEFAULT.labelResId,
      numericKeyboardActions = numericKeyboardCommander,
    )

  private fun CalculatorBottomSheetState.getSelectedCurrencyIndex(): Int =
    usedCurrencies.value.indexOfFirst { it.currencySymbolOrCode == currency }

  fun updateNoteText(newNote: String) {
    _uiState.update { it.copy(note = newNote) }
  }

  fun showConfirmDeleteTransactionBottomSheet() {
    showBottomSheet(
      GeneralBottomSheetData
        .Builder(Action.DangerAction(resourceValueOf(R.string.delete), ::deleteTransaction))
        .title(resourceValueOf(R.string.transaction_detail_dialog_delete_confirm_title))
        .negativeAction(resourceValueOf(R.string.cancel), ::hideBottomSheet)
        .build()
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

  private fun deleteTransaction() {
    val id = createTransactionRepository.getTransactionPayload()?.transactionId ?: return
    viewModelScope.launch {
      transactionRepository.deleteTransaction(id)
    }
    navigateUp()
  }

  override fun onCleared() {
    super.onCleared()
    createTransactionRepository.clear(shouldClearTarget)
  }

  fun selectTransferTargetAccount() {
    appNavigationEventBus.navigate(
      AppNavigationEvent.SelectAccount(isTransferTargetSelect = true)
    )
  }
}
