package com.emendo.accounts.create

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.CalculatorBSInput
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import com.emendo.expensestracker.core.model.data.EqualButtonState
import com.emendo.expensestracker.core.model.data.MathOperation
import com.emendo.expensestracker.core.model.data.NumKeyboardActions
import com.emendo.expensestracker.core.model.data.NumKeyboardNumber
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.triggered
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val amountFormatter: AmountFormatter,
) : BaseBottomSheetViewModel<BottomSheetType>(), NumKeyboardActions {

  private val _state = MutableStateFlow(
    CreateAccountScreenData.getDefaultState(
      defaultCurrency = CurrencyModel.USD,
      decimalSeparator = amountFormatter.decimalSeparator.toString(),
    )
  )
  val state = _state.asStateFlow()

  private val equalButtonState = MutableStateFlow(EqualButtonState.Default)
  private val initialBalanceState = MutableStateFlow(CalculatorBSInput.DEFAULT_INITIAL_BALANCE)

  private var createAccountJob: Job? = null

  private var calculatorBSInput = CalculatorBSInput(
    number1 = StringBuilder(CalculatorBSInput.DEFAULT_INITIAL_BALANCE),
    amountFormatter = amountFormatter,
    doOnValueChange = { formattedValue, equalButtonState ->
      _state.update { it.copy(initialBalance = formattedValue) }
      initialBalanceState.update { formattedValue }
      this.equalButtonState.update { equalButtonState }
    }
  )

  override fun onChangeSignClick() {
    // Todo
  }

  override fun onClearClick() {
    calculatorBSInput.onClearClick()
  }

  override fun onMathOperationClick(mathOperation: MathOperation) {
    if (calculatorBSInput.doMath(mathOperation)) return

    calculatorBSInput.input(mathOperation)
  }

  override fun onNumberClick(numKeyboardNumber: NumKeyboardNumber) {
    calculatorBSInput.input(numKeyboardNumber)
  }

  override fun onPrecisionClick() {
    calculatorBSInput.addDecimalSeparator()
  }

  override fun onDoneClick() {
    calculatorBSInput.doMathAndCleanMathOperation()
    hideBottomSheet()
  }

  override fun onEqualClick() {
    calculatorBSInput.doMath()
  }

  override fun onCurrencyClick() {}

  override fun onDismissBottomSheetRequest() {
    if (bottomSheetState.value.bottomSheetState is BottomSheetType.Calculator) {
      onDoneClick()
    }
    super.onDismissBottomSheetRequest()
  }

  fun createNewAccount() {
    if (createAccountJob != null) {
      return
    }

    createAccountJob = viewModelScope.launch {
      with(state.value) {
        accountsRepository.upsertAccount(
          Account(
            name = accountName,
            balance = amountFormatter.toAmount(initialBalance).toBigDecimal().toDouble(),
            currencyModel = currency,
            icon = icon,
            color = color,
          )
        )
        _state.update { it.copy(navigateUpEvent = triggered) }
      }
    }
  }

  fun onIconRowClick() {
    updateBottomSheet(
      BottomSheetType.Icon(
        selectedIcon = state.value.icon,
        onSelectIcon = ::setIcon,
      )
    )
  }

  fun onColorRowClick() {
    updateBottomSheet(
      BottomSheetType.Color(
        selectedColor = state.value.color,
        onSelectColor = ::setColor,
      )
    )
  }

  fun onCurrencyRowClick() {
    updateBottomSheet(
      BottomSheetType.Currency(
        selectedCurrency = state.value.currency,
        onSelectCurrency = ::setCurrency,
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
    updateBottomSheet(
      BottomSheetType.Calculator(
        text = initialBalanceState.asStateFlow(),
        initialBalanceActions = this,
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