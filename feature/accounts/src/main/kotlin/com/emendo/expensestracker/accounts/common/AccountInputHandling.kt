package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.data.mapper.CurrencyMapper
import com.emendo.expensestracker.core.model.data.CurrencyModel

class AccountInputHandling(
  private val calculatorFormatter: CalculatorFormatter,
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
  private val stateManager: AccountStateManagement,
  private val currencyMapper: CurrencyMapper,
) {

  private fun hideBottomSheet() {

  }

  fun doneClick(): Boolean {
    hideBottomSheet()
    return false
  }

  fun negate() {
    numericKeyboardCommander.negate()
  }

  fun onDoneClick() {
    numericKeyboardCommander.onDoneClick()
  }

  fun updateCurrencyByCode(code: String) {
    val currency = currencyMapper.toCurrencyModelBlocking(code)
    setCurrency(currency)
  }

  private fun setCurrency(currency: CurrencyModel) {
    val balance =
      amountFormatter.replaceCurrency(stateManager.state.value.balance, stateManager.state.value.currency, currency)
    stateManager.updateBalance(balance)
    stateManager.updateCurrency(currency)
  }
}