package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.api.CreateAccountScreenApi
import com.emendo.expensestracker.accounts.api.SelectAccountScreenApi
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.ext.getNextItem
import com.emendo.expensestracker.core.app.common.ext.stateFlow
import com.emendo.expensestracker.core.app.common.ext.stateInEagerlyList
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.domain.account.GetLastTransferAccountOrRandomUseCase
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.domain.api.getTargetOrDefault
import com.emendo.expensestracker.core.domain.currency.GetUsedCurrenciesUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorBottomSheetState
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorKeyboardActions
import com.emendo.expensestracker.create.transaction.api.CreateTransactionScreenApi
import com.emendo.expensestracker.createtransaction.transaction.data.CreateTransactionCommander
import com.emendo.expensestracker.createtransaction.transaction.data.FieldWithError
import com.emendo.expensestracker.createtransaction.transaction.data.getTransactionType
import com.emendo.expensestracker.createtransaction.transaction.domain.GetCalculatorBottomSheetDataUseCase
import com.emendo.expensestracker.createtransaction.transaction.domain.GetConvertedFormattedValueUseCase
import com.emendo.expensestracker.createtransaction.transaction.domain.GetDefaultAmountUseCase
import com.emendo.expensestracker.createtransaction.transaction.domain.GetDefaultAmountUseCase.Companion.DEFAULT_AMOUNT_VALUE
import com.emendo.expensestracker.createtransaction.transaction.domain.GetTransferReceivedAmountUseCase
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import com.emendo.expensestracker.data.api.model.transaction.TransactionType.Companion.labelResId
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.model.ui.resourceValueOf
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

private const val CREATE_TRANSACTION_DELETE_TRANSACTION_DIALOG = "create_transaction_delete_transaction_dialog"

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  getUsedCurrenciesUseCase: GetUsedCurrenciesUseCase,
  private val accountRepository: AccountRepository,
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
  private val createTransactionController: CreateTransactionController,
  private val transactionRepository: TransactionRepository,
  private val calculatorFormatter: CalculatorFormatter,
  private val appNavigationEventBus: AppNavigationEventBus,
  private val getLastTransferAccountOrRandomUseCase: GetLastTransferAccountOrRandomUseCase,
  private val getDefaultAmountUseCase: GetDefaultAmountUseCase,
  private val getConvertedFormattedValueUseCase: GetConvertedFormattedValueUseCase,
  private val getCalculatorBottomSheetDataUseCase: GetCalculatorBottomSheetDataUseCase,
  private val getTransferReceivedAmountUseCase: GetTransferReceivedAmountUseCase,
  private val createAccountScreenApi: CreateAccountScreenApi,
  private val selectAccountScreenApi: SelectAccountScreenApi,
  private val createTransactionScreenApi: CreateTransactionScreenApi,
) : ViewModel(),
    ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate(),
    CalculatorKeyboardActions,
    CreateTransactionCommander {

  private val selectedAccountId by savedStateHandle.stateFlow<Long?>(createTransactionController.getSourceSnapshot()?.id)
  private val accountsFlow: Flow<ImmutableList<AccountUiModel>> =
    combine(accountRepository.getAccounts(), selectedAccountId, ::mapAccounts)

  private val _uiState: MutableStateFlow<CreateTransactionUiState> =
    MutableStateFlow(getDefaultCreateTransactionUiState())
  internal val uiState: StateFlow<CreateTransactionUiState> = combine(
    _uiState,
    createTransactionController.getTarget(),
    createTransactionController.getSource(),
    accountsFlow,
    transform = ::combineCreateTransactionUiState,
  )
    .stateInWhileSubscribed(viewModelScope, getDefaultCreateTransactionUiState())

  private val _calculatorBottomSheetState: MutableStateFlow<CalculatorBottomSheetState> =
    MutableStateFlow(getInitialCalculatorState())

  private val _calculatorCurrencyState: MutableStateFlow<CurrencyModel?> =
    MutableStateFlow(createTransactionController.getSourceSnapshot()?.currency)
  private val sourceCurrency: Flow<CurrencyModel?> =
    sourceCurrencyFlow(createTransactionController, ::shouldKeepUserSelectedCurrency)

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
    getUsedCurrenciesUseCase().stateInEagerlyList(viewModelScope)

  private val _bottomSheetState: MutableStateFlow<CreateTransactionBottomSheetState> by lazy {
    MutableStateFlow(
      CreateTransactionBottomSheetState(
        data = getCalculatorBottomSheetDataUseCase(calculatorState, this, numericKeyboardCommander),
        show = createTransactionController.isBottomSheetVisibleOnInit(),
      )
    )
  }

  internal val bottomSheetState: StateFlow<CreateTransactionBottomSheetState> by lazy { _bottomSheetState }

  private var createTransactionJob: Job? = null
  private var shouldClearTarget = true
  private var hasSelectedCustomCurrency = false

  init {
    handleInitialTransactionAmount()

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
  override fun confirmValueChange(sheetValue: SheetValue, bottomSheetState: BottomSheetData?): Boolean {
    if (bottomSheetState?.id == CREATE_TRANSACTION_DELETE_TRANSACTION_DIALOG) {
      return super.confirmValueChange(sheetValue, bottomSheetState)
    }

    numericKeyboardCommander.doMath()
    if (sheetValue == SheetValue.Hidden) {
      _uiState.update { state ->
        state.copy(
          sourceAmountFocused = false,
          transferTargetAmountFocused = false,
        )
      }
    }
    return super.confirmValueChange(sheetValue, bottomSheetState)
  }

  override fun updateTransactionType(transactionType: TransactionType) {
    _uiState.update { state ->
      state.copy(
        screenData = state.screenData.copy(transactionType = transactionType),
        sourceAmountFocused = true,
        transferTargetAmountFocused = false,
        isCustomTransferAmount = false,
      )
    }
    _calculatorBottomSheetState.update { calculatorState ->
      calculatorState.copy(transactionTypeLabelResId = transactionType.labelResId)
    }
    numericKeyboardCommander.doMath()

    if (transactionType != TransactionType.TRANSFER) {
      createTransactionController.setTarget(createTransactionController.getDefaultTarget(transactionType))
      return
    }

    setNonTransferTransactionType()
  }

  private fun setNonTransferTransactionType() {
    viewModelScope.launch {
      val target = getLastTransferAccountOrRandomUseCase(createTransactionController.getSourceSnapshot()?.id)
      createTransactionController.setTarget(target)

      if (target == null) {
        return@launch
      }

      val source = createTransactionController.getSourceSnapshot() ?: return@launch
      val transferReceivedAmount = getConvertedFormattedValueUseCase(
        value = uiState.value.amount.value,
        fromCurrency = source.currency,
        toCurrency = target.currency,
      )

      _uiState.update { state ->
        state.copy(transferReceivedAmount = transferReceivedAmount)
      }
    }
  }

  override fun updateNoteText(newNote: String) {
    _uiState.update { it.copy(note = newNote) }
  }

  // Keyboard action
  override fun changeTransactionType() {
    if (uiState.value.screenData.transactionType == TransactionType.EXPENSE) {
      updateTransactionType(TransactionType.INCOME)
      return
    }

    updateTransactionType(TransactionType.EXPENSE)
  }

  override fun onCurrencyClick() {
    val currencies = usedCurrencies.value

    if (currencies.isEmpty()) {
      return
    }

    hasSelectedCustomCurrency = true
    val newCurrency = getNextCurrency(currencies, calculatorState.value.getSelectedCurrencyIndex())
    _calculatorCurrencyState.update { newCurrency }
  }

  private fun updateAmountCurrency(newCurrency: CurrencyModel) {
    _uiState.update { state ->
      if (state.transferTargetAmountFocused) {
        return@update updateTransferAmountCurrency(state, newCurrency)
      }

      state.copy(
        amount = amountFormatter.replaceCurrency(
          amount = state.amount,
          newCurrencyModel = newCurrency,
        )
      )
    }
  }

  private fun updateTransferAmountCurrency(
    state: CreateTransactionUiState,
    newCurrency: CurrencyModel,
  ): CreateTransactionUiState {
    val transferReceivedAmount = state.transferReceivedAmount
    if (transferReceivedAmount != null) {
      return state.copy(
        transferReceivedAmount = amountFormatter.replaceCurrency(
          amount = transferReceivedAmount,
          newCurrencyModel = newCurrency,
        )
      )
    }

    return state
  }

  override fun showCalculatorBottomSheet(sourceTrigger: Boolean) {
    numericKeyboardCommander.doMath()
    handleAmountFocus(sourceTrigger)
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

  private fun handleAmountFocus(sourceTrigger: Boolean) {
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
  }

  override fun saveTransaction() {
    val source = createTransactionController.getSourceSnapshot()

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
    val target = createTransactionController.getTargetOrDefault(transactionType)
    createTransaction(source, target)
  }

  fun getAccountListScreenRoute(): String = selectAccountScreenApi.getSelectAccountScreenRoute()

  fun getCreateAccountScreenRoute(): String = createAccountScreenApi.getCreateAccountScreenRoute()

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

  override fun hideCalculatorBottomSheet() {
    _bottomSheetState.update { sheetState ->
      sheetState.copy(hide = triggered)
    }
    _uiState.update { state ->
      state.copy(sourceAmountFocused = false)
    }
  }

  override fun selectAccount(account: AccountUiModel) {
    selectedAccountId.update { account.id }
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
        transferReceivedAmount = getTransferReceivedAmountUseCase(state, amount),
      )
    }

    return false
  }

  fun getSelectTransferTargetAccountRoute(): String =
    selectAccountScreenApi.getSelectAccountScreenRoute(isTransferTargetSelect = true)

  fun getDuplicateTransactionScreenRoute(): String {
    val payload = createTransactionController.getTransactionPayload()
      ?: throw IllegalStateException("Transaction payload must not be null")
    shouldClearTarget = false

    return createTransactionScreenApi.getRoute(
      source = createTransactionController.getSourceSnapshot(),
      target = createTransactionController.getTargetSnapshot(),
      payload = payload.copy(transactionId = null),
      shouldNavigateUp = true,
    )
  }

  override fun showConfirmDeleteTransactionBottomSheet() {
    showModalBottomSheet(
      GeneralBottomSheetData
        .Builder(
          id = CREATE_TRANSACTION_DELETE_TRANSACTION_DIALOG,
          positiveAction = Action.DangerAction(resourceValueOf(R.string.delete), ::deleteTransaction)
        )
        .title(resourceValueOf(R.string.transaction_detail_dialog_delete_confirm_title))
        .negativeAction(resourceValueOf(R.string.cancel), ::hideModalBottomSheet)
        .build()
    )
  }

  private fun deleteTransaction() {
    val id = createTransactionController.getTransactionPayload()?.transactionId ?: return
    viewModelScope.launch {
      transactionRepository.deleteTransaction(id)
    }
    hideModalBottomSheet()
    navigateUp()
  }

  override fun consumeFieldError(field: FieldWithError) {
    _uiState.updateScreenData { state ->
      when (field) {
        is FieldWithError.Amount -> state.copy(amountError = consumed)
        is FieldWithError.Source -> state.copy(sourceError = consumed)
      }
    }
  }

  override fun consumeShowCalculatorBottomSheet() {
    _bottomSheetState.update { it.copy(show = consumed) }
  }

  override fun consumeHideCalculatorBottomSheet() {
    _bottomSheetState.update { it.copy(hide = consumed) }
  }

  private fun getSelectedCurrency(state: CreateTransactionUiState): CurrencyModel {
    val screenData = state.screenData
    val currency: CurrencyModel = if (screenData.transactionType == TransactionType.TRANSFER) {
      when {
        state.sourceAmountFocused -> state.amount.currency
        state.transferTargetAmountFocused -> checkNotNull(state.transferReceivedAmount?.currency) {
          "Transfer target amount must not be focused with null Amount"
        }

        else -> state.amount.currency
      }
    } else {
      state.amount.currency
    }
    return currency
  }

  private fun combineCreateTransactionUiState(
    uiState: CreateTransactionUiState,
    target: TransactionTarget?,
    source: TransactionSource?,
    accounts: ImmutableList<AccountUiModel>,
  ): CreateTransactionUiState {
    val screenData = uiState.screenData

    return uiState.copy(
      target = target.orDefault(screenData.transactionType),
      source = source?.toTransactionItemModel(),
      accounts = accounts,
    )
  }

  private fun mapAccounts(
    accounts: List<AccountModel>,
    selectedAccountId: Long?,
  ): ImmutableList<AccountUiModel> = accounts.map {
    AccountUiModel(
      id = it.id,
      name = it.name,
      icon = it.icon,
      selected = it.id == selectedAccountId,
    )
  }.toPersistentList()

  /**
   * Checks if the user really changed Amount. Prevents state change on just focusing transfer target amount
   */
  private fun CreateTransactionUiState.newCustomTransferAmountValue(amount: Amount): Boolean =
    if (isCustomTransferAmount) {
      true
    } else {
      transferReceivedAmount != amount
    }

  private fun shouldKeepUserSelectedCurrency(): Boolean {
    if (uiState.value.amount.value == DEFAULT_AMOUNT_VALUE) {
      return false
    }
    return hasSelectedCustomCurrency
  }

  private fun TransactionTarget?.orDefault(transactionType: TransactionType): TransactionItemModel {
    val target = this ?: createTransactionController.getDefaultTarget(transactionType)
    return target.toTransactionItemModel()
  }

  private fun handleInitialTransactionAmount() {
    createTransactionController.getTransactionPayload()?.transactionAmount?.let { transactionValue ->
      numericKeyboardCommander.setInitialValue(calculatorFormatter.formatFinalWithPrecision(transactionValue.value))
    }
  }

  private fun getDefaultCreateTransactionUiState(): CreateTransactionUiState {
    val payload = createTransactionController.getTransactionPayload()
    val source = createTransactionController.getSourceSnapshot()
    return CreateTransactionUiState(
      amount = payload?.transactionAmount ?: getDefaultAmountUseCase(source?.currency),
      screenData = CreateTransactionScreenData(transactionType = getTransactionType(payload)),
      target = createTransactionController.getTargetOrDefault(TransactionType.DEFAULT).toTransactionItemModel(),
      source = source?.toTransactionItemModel(),
      note = payload?.note,
      sourceAmountFocused = payload == null,
      transferReceivedAmount = payload?.transferReceivedAmount,
      accounts = mapAccounts(
        accounts = accountRepository.getAccountsSnapshot(),
        selectedAccountId = createTransactionController.getSourceSnapshot()?.id,
      ),
    )
  }

  private fun getInitialCalculatorState(): CalculatorBottomSheetState =
    CalculatorBottomSheetState.initial(
      transactionTypeLabelResId = TransactionType.DEFAULT.labelResId,
      numericKeyboardActions = numericKeyboardCommander,
    )

  private fun CalculatorBottomSheetState.getSelectedCurrencyIndex(): Int =
    usedCurrencies.value.indexOfFirst { it.currencySymbolOrCode == currency }

  override fun onCleared() {
    super.onCleared()
    createTransactionController.clear(shouldClearTarget)
  }
}

internal fun CreateTransactionController.isBottomSheetVisibleOnInit(): StateEvent =
  if (getTransactionPayload() == null) triggered else consumed

internal fun sourceCurrencyFlow(
  createTransactionController: CreateTransactionController,
  shouldKeepUserSelectedCurrency: () -> Boolean,
): Flow<CurrencyModel?> =
  createTransactionController.getSource()
    .transform { source ->
      if (!shouldKeepUserSelectedCurrency()) {
        emit(source?.currency)
      }
    }
    .distinctUntilChanged()

internal fun getNextCurrency(currencies: List<CurrencyModel>, selectedCurrencyIndex: Int): CurrencyModel =
  currencies.getNextItem(selectedCurrencyIndex)