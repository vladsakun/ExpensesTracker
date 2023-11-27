package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.common.ext.enableReloadWhenSubscribed
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.di.DecimalSeparator
import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
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
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
  private val currencyCacheManager: CurrencyCacheManager,
  @DecimalSeparator private val decimalSeparator: String,
  private val createTransactionRepository: CreateTransactionRepository,
  private val transactionRepository: TransactionRepository,
  private val calculatorFormatter: CalculatorFormatter,
  private val appNavigationEventBus: AppNavigationEventBus,
) : BaseBottomSheetViewModel<BottomSheetType.Calculator>(), CalculatorKeyboardActions {

  //  private val currencyUiState: MutableStateFlow<CurrencyModel?> = MutableStateFlow(null)
  //  private val currencyState: StateFlow<CurrencyModel?> = merge(currencyUiState, sourceUiModel.map { it?.currency })
  //    .distinctUntilChanged()
  //    .stateIn(
  //      scope = viewModelScope,
  //      started = SharingStarted.Eagerly,
  //      initialValue = null,
  //    )

  private val _uiState: MutableStateFlow<CreateTransactionUiState> =
    MutableStateFlow(getDefaultCreateTransactionUiState())

  private val target: Flow<TransactionTarget?> by lazy(LazyThreadSafetyMode.NONE) { getTargetFlow() }
  private val source: Flow<TransactionSource?> by lazy(LazyThreadSafetyMode.NONE) { getSourceFlow() }

  val uiState: StateFlow<CreateTransactionUiState> =
    _uiState.asStateFlow()
      .enableReloadWhenSubscribed(target, source)
      .stateInWhileSubscribed(scope = viewModelScope, initialValue = getDefaultCreateTransactionUiState())

  private val _calculatorBottomSheetState: MutableStateFlow<CalculatorBottomSheetState> =
    MutableStateFlow(getInitialCalculatorState())

  private val calculatorText: Flow<String> by lazy(LazyThreadSafetyMode.NONE) { getCalculatorTextStateFlow() }
  private val equalButtonState: Flow<EqualButtonState> by lazy(LazyThreadSafetyMode.NONE) { getEqualButtonStateFlow() }

  private val calculatorState: StateFlow<CalculatorBottomSheetState> =
    _calculatorBottomSheetState.asStateFlow()
      .enableReloadWhenSubscribed(calculatorText, equalButtonState)
      .stateInWhileSubscribed(scope = viewModelScope, initialValue = getInitialCalculatorState())

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
  }

  override fun changeTransactionType() {
    val transactionType = uiState.value.successValue?.screenData?.transactionType ?: return

    if (transactionType == TransactionType.EXPENSE) {
      changeTransactionType(TransactionType.INCOME)
      return
    }

    changeTransactionType(TransactionType.EXPENSE)
  }

  override fun onCurrencyClick() {}

  override fun onCleared() {
    super.onCleared()
    createTransactionRepository.clear()
  }

  fun showCalculatorBottomSheet() {
    showBottomSheet(
      BottomSheetType.Calculator(
        state = calculatorState,
        actions = this,
        numericKeyboardActions = numericKeyboardCommander,
        decimalSeparator = decimalSeparator,
      )
    )
  }

  fun openCategoryListScreen() {

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

  private fun createTransaction(source: TransactionSource, target: TransactionTarget): Boolean {
    if (createTransactionJob != null) {
      return false
    }

    createTransactionJob = viewModelScope.launch {
      transactionRepository.createTransaction(
        source = source,
        target = target,
        amount = numericKeyboardCommander.currencyValue,
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
          calculatorFormatter.toBigDecimal(amount),
          currencyCacheManager.getGeneralCurrencySnapshot(),
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

  private fun getDefaultCreateTransactionUiState(): CreateTransactionUiState =
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
    CalculatorBottomSheetState.initial(decimalSeparator, numericKeyboardCommander)
}
