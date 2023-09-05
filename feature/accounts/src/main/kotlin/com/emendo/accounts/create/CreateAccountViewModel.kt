package com.emendo.accounts.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.AmountFormatter
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import com.emendo.expensestracker.core.model.data.MathOperation
import com.emendo.expensestracker.core.model.data.NumKeyboardActions
import com.emendo.expensestracker.core.model.data.NumKeyboardNumber
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.EqualButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val amountFormatter: AmountFormatter,
) : ViewModel(), NumKeyboardActions {

  private val _state = MutableStateFlow(
    CreateAccountScreenData.getDefaultState(
      defaultCurrency = CurrencyModel.USD,
      decimalSeparator = amountFormatter.decimalSeparator.toString(),
    )
  )
  val state = _state.asStateFlow()

  private val _bottomSheetState = MutableStateFlow<BottomSheetType?>(null)
  val bottomSheet = _bottomSheetState.asStateFlow()

  private val equalButtonState = MutableStateFlow(EqualButtonState.Default)
  private val initialBalanceState = MutableStateFlow(CreateAccountScreenData.DEFAULT_INITIAL_BALANCE)

  private val hideBottomSheetChannel = Channel<Unit>(Channel.CONFLATED)
  val hideBottomSheetEvent = hideBottomSheetChannel.receiveAsFlow()

  private val navigateUpChannel = Channel<Unit>(Channel.CONFLATED)
  val navigateUpEvent = navigateUpChannel.receiveAsFlow()

  private var calculatorBSInput = CalculatorBSInput(
    number1 = StringBuilder(CreateAccountScreenData.DEFAULT_INITIAL_BALANCE),
    amountFormatter = amountFormatter,
    doOnValueChange = { formattedValue, equalButtonState ->
      _state.update { it.copy(initialBalance = formattedValue) }
      initialBalanceState.update { formattedValue }
      this.equalButtonState.update { equalButtonState }
    }
  )

  override fun onChangeSignClick() {
  }

  override fun onClearClick() {
    calculatorBSInput.onClearClick()
  }

  override fun onMathOperationClick(mathOperation: MathOperation) {
    if (calculatorBSInput.doMath(mathOperation)) return

    calculatorBSInput.mMathOperation = mathOperation
  }

  override fun onNumberClick(numKeyboardNumber: NumKeyboardNumber) {
    calculatorBSInput.endNumber(numKeyboardNumber)
  }

  override fun onPrecisionClick() {
    calculatorBSInput.addDecimalSeparator()
  }

  override fun onDoneClick() {
    calculatorBSInput.doOnDoneClick()
    hideBottomSheetChannel.trySend(Unit)
  }

  override fun onEqualClick() {
    calculatorBSInput.doMath()
  }

  override fun onCurrencyClick() {}

  fun createNewAccount() {
    viewModelScope.launch {
      with(state.value) {
        accountsRepository.upsertAccount(
          Account(
            name = accountName,
            balance = amountFormatter.toAmount(initialBalance, currency.currencyName).toBigDecimal().toDouble(),
            currencyModel = currency,
            icon = icon,
            color = color,
          )
        )
        navigateUpChannel.send(Unit)
      }
    }
  }

  fun onIconRowClick() {
    _bottomSheetState.update {
      BottomSheetType.Icon(
        selectedIcon = state.value.icon,
        onSelectIcon = ::setIcon,
      )
    }
  }

  fun onColorRowClick() {
    _bottomSheetState.update {
      BottomSheetType.Color(
        selectedColor = state.value.color,
        onSelectColor = ::setColor,
      )
    }
  }

  fun onCurrencyRowClick() {
    _bottomSheetState.update {
      BottomSheetType.Currency(
        selectedCurrency = state.value.currency,
        onSelectCurrency = ::setCurrency,
      )
    }
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
    _bottomSheetState.update {
      BottomSheetType.Calculator(
        text = initialBalanceState.asStateFlow(),
        initialBalanceActions = this,
        decimalSeparator = amountFormatter.decimalSeparator.toString(),
        equalButtonState = equalButtonState.asStateFlow(),
        currency = state.value.currency.currencyName,
      )
    }
  }

  fun onDismissRequest() {
    if (bottomSheet.value is BottomSheetType.Calculator) {
      onDoneClick()
    }
    _bottomSheetState.update { null }
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