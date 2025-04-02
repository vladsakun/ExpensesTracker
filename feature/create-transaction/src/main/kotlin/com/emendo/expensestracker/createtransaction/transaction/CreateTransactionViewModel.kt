package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.api.CreateAccountScreenApi
import com.emendo.expensestracker.accounts.api.SelectAccountArgs
import com.emendo.expensestracker.accounts.api.SelectAccountResult
import com.emendo.expensestracker.accounts.api.SelectAccountScreenApi
import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.ext.getNextItem
import com.emendo.expensestracker.core.app.common.ext.stateInEagerlyList
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_TRANSFER_TRANSACTION
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.domain.api.getTargetOrNonTransferDefault
import com.emendo.expensestracker.core.domain.currency.GetUsedCurrenciesUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.model.data.TransactionType.Companion.toTransactionType
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
import com.emendo.expensestracker.createtransaction.transaction.domain.GetCalculatorBottomSheetDataUseCase
import com.emendo.expensestracker.createtransaction.transaction.domain.GetDefaultAmountUseCase.Companion.DEFAULT_AMOUNT_VALUE
import com.emendo.expensestracker.createtransaction.transaction.domain.TransactionFacade
import com.emendo.expensestracker.data.api.GetTransactionTypeUseCase
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.data.api.extensions.labelResId
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
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

// TODO dev break 22.03
// 2 - why Amount filed disappear on transfer type?

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  getUsedCurrenciesUseCase: GetUsedCurrenciesUseCase,
  private val accountRepository: AccountRepository,
  private val transactionRepository: TransactionRepository,
  private val amountFormatter: AmountFormatter,
  private val calculatorFormatter: CalculatorFormatter,
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val createTransactionController: CreateTransactionController,
  private val createAccountScreenApi: CreateAccountScreenApi,
  private val selectAccountScreenApi: SelectAccountScreenApi,
  private val createTransactionScreenApi: CreateTransactionScreenApi,
  private val transactionFacade: TransactionFacade,
  private val getCalculatorBottomSheetDataUseCase: GetCalculatorBottomSheetDataUseCase,
  private val getTransactionTypeUseCase: GetTransactionTypeUseCase,
) : ViewModel(),
    ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate(),
    CalculatorKeyboardActions,
    CreateTransactionCommander {

  private val accountsFlow: Flow<ImmutableList<AccountUiModel>> =
    combine(accountRepository.getAccounts(), createTransactionController.getSource().map { it?.id }, ::mapAccounts)

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
        bottomSheetData = getCalculatorBottomSheetDataUseCase(calculatorState, this, numericKeyboardCommander),
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
      createTransactionController.setTarget(createTransactionController.getDefaultNonTransferTarget(transactionType))
      return
    }

    setTransferTransactionType()
  }

  private fun setTransferTransactionType() {
    viewModelScope.launch {
      val target = transactionFacade.getLastTransferAccountOrFirst(createTransactionController.getSourceSnapshot()?.id)
      createTransactionController.setTarget(target)

      target ?: return@launch

      val source = createTransactionController.getSourceSnapshot() ?: return@launch
      val transferReceivedAmount = transactionFacade.getConvertedFormattedValue(
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
    val target = createTransactionController.getTargetOrNonTransferDefault(transactionType)

    if (target == null) {
      _uiState.updateScreenData { screenData ->
        if (transactionType == TransactionType.TRANSFER) {
          screenData.copy(transferTargetError = triggered)
        } else {
          screenData.copy(targetError = triggered)
        }
      }
      return
    }

    createTransaction(source, target)
  }

  fun updateSelectedAccount(result: SelectAccountResult) {
    val account = accountRepository.getByIdSnapshot(result.accountId) ?: return
    if (result.isSource) updateSourceAccount(account) else updateTargetAccount(account)
  }

  private fun updateTargetAccount(account: AccountModel) {
    if (account == createTransactionController.getSourceSnapshot()) {
      createTransactionController.setSource(null)
    }

    createTransactionController.setTarget(account)
  }

  private fun updateSourceAccount(account: AccountModel) {
    if (account == createTransactionController.getTargetSnapshot()) {
      createTransactionController.setTarget(null)
    }

    createTransactionController.setSource(account)
  }

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

  override fun selectSourceAccount(account: AccountUiModel) {
    val source = accountRepository.getByIdSnapshot(account.id)
    createTransactionController.setSource(source)

    if (source?.id == createTransactionController.getTargetSnapshot()?.id) {
      createTransactionController.setTarget(null)
    }
  }

  override fun selectTargetAccount(account: AccountUiModel) {
    val target = accountRepository.getByIdSnapshot(account.id)
    createTransactionController.setTarget(target)

    if (target?.id == createTransactionController.getSourceSnapshot()?.id) {
      createTransactionController.setSource(null)
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
        transferReceivedAmount = transactionFacade.getTransferReceivedAmount(state, amount),
      )
    }

    return false
  }

  fun getSelectAccountScreenRoute(isSource: Boolean): String {
    val selectedAccount = if (isSource) {
      createTransactionController.getSourceSnapshot()
    } else {
      createTransactionController.getTargetSnapshot()
    }

    return selectAccountScreenApi.getSelectAccountScreenRoute(
      SelectAccountArgs(
        isSource = isSource,
        selectedAccountId = selectedAccount?.id
      )
    )
  }

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
        .title(resourceValueOf(R.string.dialog_transaction_detail_delete_confirm_title))
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
        is FieldWithError.TransferTarget -> state.copy(transferTargetError = consumed)
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
      // If it is a transfer transaction, target should be account without default value
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

  private fun TransactionTarget?.orDefault(transactionType: TransactionType): TransactionItemModel? {
    val target = this ?: createTransactionController.getDefaultNonTransferTarget(transactionType)
    return target?.toTransactionItemModel()
  }

  private fun handleInitialTransactionAmount() {
    createTransactionController.getTransactionPayload()?.transactionAmount?.let { transactionValue ->
      numericKeyboardCommander.setInitialValue(calculatorFormatter.formatFinalWithPrecision(transactionValue.value))
    }
  }

  private fun getDefaultCreateTransactionUiState(): CreateTransactionUiState {
    val payload = createTransactionController.getTransactionPayload()
    val source = createTransactionController.getSourceSnapshot()
    val target = createTransactionController.getTargetSnapshot()

    val transactionType = if (target != null && source != null) {
      getTransactionTypeUseCase(source, target)
    } else {
      getTransactionType(payload)
    }

    return CreateTransactionUiState(
      amount = payload?.transactionAmount ?: transactionFacade.getDefaultAmount(source?.currency),
      screenData = CreateTransactionScreenData(transactionType = transactionType),
      target = createTransactionController.getTargetOrNonTransferDefault(TransactionType.DEFAULT)
        ?.toTransactionItemModel(),
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

internal fun getTransactionType(payload: CreateTransactionEventPayload?): TransactionType {
  if (IS_DEBUG_TRANSFER_TRANSACTION) {
    return TransactionType.TRANSFER
  }

  return payload?.transactionType?.toTransactionType() ?: TransactionType.DEFAULT
}