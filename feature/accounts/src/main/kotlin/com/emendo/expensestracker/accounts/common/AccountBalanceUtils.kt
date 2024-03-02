package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.model.ui.dataValue

interface AccountBalanceUtils {
  val accountStateManager: AccountStateManager<*>
  val amountFormatter: AmountFormatter
  val calculatorFormatter: CalculatorFormatter

  fun updateCurrencyByCode(code: String) {
    val stateData = accountStateManager.state.value.dataValue() ?: return

    val currency = CurrencyModel.toCurrencyModel(code)
    val balance = amountFormatter.replaceCurrency(stateData.balance, currency)
    accountStateManager.updateBalance(balance)
    accountStateManager.updateCurrency(currency)
  }
}