package com.emendo.expensestracker.budget.create

import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter

interface CreateBudgetBottomSheetContract : InitialBalanceKeyboardActions {
  val budgetStateManager: BudgetStateManager<*>
  val modalBottomSheetStateManager: ModalBottomSheetStateManager
  val numericKeyboardCommander: NumericKeyboardCommander
  val calculatorFormatter: CalculatorFormatter
  val amountFormatter: AmountFormatter

  override fun onChangeSignClick() {
    numericKeyboardCommander.negate()
  }

  fun showLimitBottomSheet(currency: CurrencyModel) {
    numericKeyboardCommander.setCallbacks(
      doneClick = ::doneClick,
      onMathDone = { updateLimitValue(it, currency) },
    )
    modalBottomSheetStateManager.showModalBottomSheet(
      BudgetLimitBottomSheetData(
        value = numericKeyboardCommander.calculatorTextState,
        onValueChanged = { updateLimitValue(it, currency) },
        actions = this,
        equalButtonState = numericKeyboardCommander.equalButtonState,
        decimalSeparator = calculatorFormatter.decimalSeparator.toString(),
        currency = currency.currencyCode,
        numericKeyboardActions = numericKeyboardCommander,
      )
    )
  }

  private fun updateLimitValue(value: String, currency: CurrencyModel): Boolean {
    val formattedValue = amountFormatter.format(
      amount = calculatorFormatter.toBigDecimal(value),
      currency = currency,
    )
    budgetStateManager.updateLimit(formattedValue)
    return false
  }

  private fun doneClick(): Boolean {
    modalBottomSheetStateManager.hideModalBottomSheet()
    return false
  }
}
