package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.InitialBalanceKeyboardActions

// Todo use composition over inheritance
abstract class AccountViewModel(
  private val calculatorFormatter: CalculatorFormatter,
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
) : BaseBottomSheetViewModel<BottomSheetType>(), InitialBalanceKeyboardActions, AccountStateManagement {

  init {
    numericKeyboardCommander.setCallbacks(doneClick = ::doneClick, onMathDone = ::updateValue)
  }

  override fun onChangeSignClick() {
    numericKeyboardCommander.negate()
  }

  override fun dismissBottomSheet() {
    if (bottomSheetState.value.bottomSheetState is BottomSheetType.Balance) {
      numericKeyboardCommander.onDoneClick()
    }
    super.dismissBottomSheet()
  }

  fun showBalanceBottomSheet() {
    showBottomSheet(
      BottomSheetType.Balance(
        text = numericKeyboardCommander.calculatorTextState,
        actions = this,
        decimalSeparator = calculatorFormatter.decimalSeparator.toString(),
        equalButtonState = numericKeyboardCommander.equalButtonState,
        currency = state.value.currency.currencyName,
        numericKeyboardActions = numericKeyboardCommander,
      )
    )
  }

  private fun updateValue(value: String): Boolean {
    val formattedValue = amountFormatter.format(calculatorFormatter.toBigDecimal(value), state.value.currency)
    updateBalance(formattedValue)
    return false
  }

  private fun doneClick(): Boolean {
    hideBottomSheet()
    return false
  }
}