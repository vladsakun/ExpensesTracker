package com.emendo.expensestracker.accounts.create

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.CalculatorInput
import com.emendo.expensestracker.core.data.DEFAULT_CALCULATOR_TEXT
import com.emendo.expensestracker.core.data.KeyboardCallbacks
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountsRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumKeyboardNumber
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.calculator.InitialBalanceKeyboardActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val amountFormatter: AmountFormatter,
  private val calculatorInput: CalculatorInput,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val currencyCacheManager: CurrencyCacheManager,
) : BaseBottomSheetViewModel<BottomSheetType>(),
    // Todo extract to commons
    InitialBalanceKeyboardActions,
    KeyboardCallbacks {

  //Todo maybe allow screen state with nullable currency
  private val _state = MutableStateFlow(
    CreateAccountScreenData.getDefaultState(
      currency = currencyCacheManager.getGeneralCurrencySnapshot()
    )
  )

  val state = _state.asStateFlow()

  private val equalButtonState = MutableStateFlow(EqualButtonState.Default)
  private val initialBalanceState = MutableStateFlow(DEFAULT_CALCULATOR_TEXT)

  private var createAccountJob: Job? = null

  // Todo extract to commons
  init {
    calculatorInput.initCallbacks(this)

    if (IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET) {
      viewModelScope.launch {
        delay(100)
        showBottomSheet(
          BottomSheetType.InitialBalance(
            text = initialBalanceState.asStateFlow(),
            actions = this@CreateAccountViewModel,
            decimalSeparator = amountFormatter.decimalSeparator.toString(),
            equalButtonState = equalButtonState.asStateFlow(),
            currency = state.value.currency.currencyName,
          )
        )
      }
    }
  }

  // Todo extract to commons
  override fun doOnValueChange(formattedValue: String, equalButtonState: EqualButtonState) {
    _state.update { it.copy(initialBalance = formattedValue) }
    initialBalanceState.update { formattedValue }
    this.equalButtonState.update { equalButtonState }
  }

  override fun onChangeSignClick() {
    // Todo
  }

  override fun onClearClick() {
    calculatorInput.onClearClick()
  }

  override fun onMathOperationClick(mathOperation: MathOperation) {
    if (calculatorInput.doMath(mathOperation)) return

    calculatorInput.input(mathOperation)
  }

  override fun onNumberClick(numKeyboardNumber: NumKeyboardNumber) {
    calculatorInput.input(numKeyboardNumber)
  }

  override fun onPrecisionClick() {
    calculatorInput.onPrecisionClick()
  }

  override fun onDoneClick() {
    calculatorInput.onDoneClick()
    hideBottomSheet()
  }

  override fun onEqualClick() {
    calculatorInput.doMath()
  }

  override fun onDismissBottomSheetRequest() {
    if (bottomSheetState.value.bottomSheetState is BottomSheetType.InitialBalance) {
      onDoneClick()
    }
    super.onDismissBottomSheetRequest()
  }

  fun createNewAccount() {
    if (createAccountJob != null) {
      return
    }

    createAccountJob = viewModelScope.launch(ioDispatcher) {
      with(state.value) {
        accountsRepository.upsertAccount(
          AccountModel(
            name = accountName,
            balance = amountFormatter.toBigDecimal(initialBalance),
            currency = currency,
            icon = icon,
            color = color,
          )
        )
        navigateUp()
      }
    }
  }

  fun onIconRowClick() {
    showBottomSheet(
      BottomSheetType.Icon(
        selectedIcon = state.value.icon,
        onSelectIcon = ::setIcon,
      )
    )
  }

  fun onColorRowClick() {
    showBottomSheet(
      BottomSheetType.Color(
        selectedColor = state.value.color,
        onSelectColor = ::setColor,
      )
    )
  }

  fun onCurrencyRowClick() {
    showBottomSheet(
      BottomSheetType.Currency(
        selectedCurrency = state.value.currency,
        onSelectCurrency = ::setCurrency,
        currencies = currencyCacheManager.getCurrenciesBlocking().values.toImmutableList(),
      )
    )
  }

  fun setAccountName(accountName: String) {
    _state.update {
      it.copy(
        accountName = accountName,
        isCreateAccountButtonEnabled = accountName.isNotBlank(),
      )
    }
  }

  fun onInitialBalanceClick() {
    showBottomSheet(
      BottomSheetType.InitialBalance(
        text = initialBalanceState.asStateFlow(),
        actions = this,
        decimalSeparator = amountFormatter.decimalSeparator.toString(),
        equalButtonState = equalButtonState.asStateFlow(),
        currency = state.value.currency.currencyName,
      )
    )
  }

  private fun setCurrency(currency: CurrencyModel) {
    _state.update { it.copy(currency = currency) }
  }

  private fun setIcon(icon: IconModel) {
    _state.update { it.copy(icon = icon) }
  }

  private fun setColor(color: ColorModel) {
    _state.update { it.copy(color = color) }
  }
}