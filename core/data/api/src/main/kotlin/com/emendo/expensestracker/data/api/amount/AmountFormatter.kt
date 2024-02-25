package com.emendo.expensestracker.data.api.amount

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

interface AmountFormatter {
  /**
   * @param amount
   * @return the formatted amount with the currency symbol and decimals.
   */
  fun format(amount: BigDecimal, currency: CurrencyModel): Amount

  fun replaceCurrency(amount: Amount, newCurrencyModel: CurrencyModel): Amount
}

