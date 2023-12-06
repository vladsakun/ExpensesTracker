package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.common.ext.enableReloadWhenSubscribed
import com.emendo.expensestracker.core.app.common.ext.getNextItem
import com.emendo.expensestracker.core.app.common.ext.stateInEagerlyList
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.di.DecimalSeparator
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.labelResId
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import com.emendo.expensestracker.core.domain.currency.GetUsedCurrenciesUseCase
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManagerDelegate
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
) : ViewModel(), BottomSheetStateManager by BottomSheetStateManagerDelegate(), CalculatorKeyboardActions {

  private val _uiState: MutableStateFlow<CreateTransactionUiState> =
    MutableStateFlow(getDefaultCreateTransactionUiState())

  private val source: Flow<TransactionSource?> by lazy(LazyThreadSafetyMode.NONE) { getSourceFlow() }

  val uiState: StateFlow<CreateTransactionUiState> =
    _uiState.asStateFlow()
      .enableReloadWhenSubscribed(getTargetFlow(), getSourceFlow())
      .stateInWhileSubscribed(viewModelScope, getDefaultCreateTransactionUiState())

  private val _calculatorBottomSheetState: MutableStateFlow<CalculatorBottomSheetState> =
    MutableStateFlow(getInitialCalculatorState())

  private val calculatorState: StateFlow<CalculatorBottomSheetState> =
    _calculatorBottomSheetState.asStateFlow()
      .enableReloadWhenSubscribed(getCalculatorTextStateFlow(), getEqualButtonStateFlow(), getCurrencyState())
      .stateInWhileSubscribed(viewModelScope, getInitialCalculatorState())

  private val usedCurrencies: StateFlow<List<CurrencyModel>> =
    getUsedCurrenciesUseCase().stateInEagerlyList(viewModelScope)

  private val selectedCurrencyModel: CurrencyModel
    get() = usedCurrencies.value.getOrNull(calculatorState.value.getSelectedCurrencyIndex())
      ?: currencyCacheManager.getGeneralCurrencySnapshot()

  private var createTransactionJob: Job? = null

  init {
    numericKeyboardCommander.setCallbacks(
      doneClick = ::doneClick,
      onMathDone = ::updateAmountText
    )
    showCalculatorBottomSheet()
  }

  @ExperimentalMaterial3Api
  override fun confirmValueChange(sheetValue: SheetValue): Boolean {
    numericKeyboardCommander.doMath()
    return super.confirmValueChange(sheetValue)
  }

  fun changeTransactionType(type: TransactionType) {
    _uiState.updateScreenData { state ->
      state.copy(transactionType = type)
    }
    createTransactionRepository.setTarget(createTransactionRepository.getDefaultTarget(type))
    _calculatorBottomSheetState.update { calculatorState ->
      calculatorState.copy(transactionTypeLabelResId = type.labelResId)
    }
  }

  override fun changeTransactionType() {
    val transactionType = uiState.value.successValue?.screenData?.transactionType ?: return

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

    _calculatorBottomSheetState.update { calculatorState ->
      val selectedCurrencyIndex = calculatorState.getSelectedCurrencyIndex()
      val nextCurrency = currencies.getNextItem(selectedCurrencyIndex)
      _uiState.updateScreenData { screenData ->
        screenData.copy(
          amount = amountFormatter.replaceCurrency(
            s = screenData.amount,
            oldCurrency = selectedCurrencyModel,
            newCurrencyModel = nextCurrency,
          )
        )
      }
      calculatorState.copy(currency = nextCurrency.currencySymbolOrCode)
    }
  }

  override fun onCleared() {
    super.onCleared()
    createTransactionRepository.clear()
  }

  fun showCalculatorBottomSheet() {
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

    if (source == null) {
      _uiState.updateScreenData { screenData ->
        screenData.copy(sourceError = triggered)
      }
      return
    }

    val transactionType = checkNotNull(uiState.value.successValue).screenData.transactionType
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
    appNavigationEventBus.navigate(AppNavigationEvent.SelectAccount)
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

  private fun updateAmountText(amount: String): Boolean {
    _uiState.updateScreenData { screenData ->
      screenData.copy(
        amount = amountFormatter.format(
          amount = calculatorFormatter.toBigDecimal(amount),
          currency = selectedCurrencyModel,
        )
      )
    }

    return false
  }

  // Todo total mess 💩
  private fun getTargetFlow(): Flow<TransactionTarget?> =
    createTransactionRepository.getTarget()
      .onEach { newTarget ->
        _uiState.updateIfSuccess { state ->
          val transactionType = state.screenData.transactionType
          val target = newTarget.orDefault(transactionType)
          state.copy(target = target)
        }
      }

  private fun getSourceFlow(): Flow<TransactionSource?> =
    createTransactionRepository.getSource()
      .onEach { newSource ->
        _uiState.updateIfSuccess { state ->
          val source = newSource?.toTransactionItemModel()
          state.copy(source = source)
        }
      }

  private fun getCalculatorTextStateFlow(): Flow<String> =
    numericKeyboardCommander.calculatorTextState
      .onEach { calculatorText ->
        _calculatorBottomSheetState.update { state ->
          state.copy(text = calculatorText)
        }
      }

  private fun getEqualButtonStateFlow(): Flow<EqualButtonState> =
    numericKeyboardCommander.equalButtonState
      .onEach { equalButtonState ->
        _calculatorBottomSheetState.update { state ->
          state.copy(equalButtonState = equalButtonState)
        }
      }

  private fun getDefaultCreateTransactionUiState(): CreateTransactionUiState.DisplayTransactionData =
    CreateTransactionUiState.DisplayTransactionData(
      screenData = getDefaultScreenData(),
      target = createTransactionRepository.getTargetSnapshot().orDefault(TransactionType.DEFAULT),
      source = createTransactionRepository.getSourceSnapshot()?.toTransactionItemModel(),
    )

  private fun TransactionTarget?.orDefault(transactionType: TransactionType): TransactionItemModel {
    val target = this ?: getTargetDefaultValue(transactionType)
    return target.toTransactionItemModel()
  }

  private fun getDefaultScreenData() = CreateTransactionScreenData(
    amount = amountFormatter.format(BigDecimal.ZERO, currencyCacheManager.getGeneralCurrencySnapshot()),
    transactionType = TransactionType.DEFAULT,
  )

  private fun getTargetDefaultValue(transactionType: TransactionType) =
    if (transactionType == TransactionType.TRANSFER) TODO("Get last account from transfer")
    else createTransactionRepository.getDefaultTarget(transactionType)

  private fun getInitialCalculatorState() =
    CalculatorBottomSheetState.initial(
      decimalSeparator = decimalSeparator,
      transactionTypeLabelResId = getDefaultScreenData().transactionType.labelResId,
      numericKeyboardActions = numericKeyboardCommander,
    )

  private fun getCurrencyState(): Flow<CurrencyModel?> =
    source.map { it?.currency }
      .distinctUntilChanged()
      .onEach { currency ->
        _calculatorBottomSheetState.update { calculatorState ->
          calculatorState.copy(currency = currency?.currencySymbolOrCode)
        }
      }

  private fun CalculatorBottomSheetState.getSelectedCurrencyIndex(): Int =
    usedCurrencies.value.indexOfFirst { it.currencySymbolOrCode == currency }
}
