package com.emendo.expensestracker.accounts.common.bottomsheet

import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.ui.bottomsheet.BalanceBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.model.ui.dataValue

interface AccountBottomSheetContract : InitialBalanceKeyboardActions {
  val accountStateManager: AccountStateManager<*>
  val modalBottomSheetStateManager: ModalBottomSheetStateManager

  val numericKeyboardCommander: NumericKeyboardCommander
  val calculatorFormatter: CalculatorFormatter
  val amountFormatter: AmountFormatter

  override fun onChangeSignClick() {
    numericKeyboardCommander.negate()
  }

  fun showBalanceBottomSheet() {
    val dataValue = accountStateManager.state.value.dataValue() ?: return

    // Todo Maybe move to a base view model with init
    numericKeyboardCommander.setCallbacks(doneClick = ::doneClick, onMathDone = ::updateBalanceValue)

    modalBottomSheetStateManager.showModalBottomSheet(
      BalanceBottomSheetData(
        text = numericKeyboardCommander.calculatorTextState,
        actions = this,
        decimalSeparator = calculatorFormatter.decimalSeparator.toString(),
        equalButtonState = numericKeyboardCommander.equalButtonState,
        currency = dataValue.currency.currencyName,
        numericKeyboardActions = numericKeyboardCommander,
      )
    )
  }

  private fun updateBalanceValue(value: String): Boolean {
    val dataValue = accountStateManager.state.value.dataValue() ?: return true

    val formattedValue = amountFormatter.format(
      amount = calculatorFormatter.toBigDecimal(value),
      currency = dataValue.currency,
    )
    accountStateManager.updateBalance(formattedValue)
    return false
  }

  private fun doneClick(): Boolean {
    modalBottomSheetStateManager.hideModalBottomSheet()
    return false
  }
}