package com.emendo.expensestracker.core.data.amount

import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

interface AmountFormatter {
  /**
   * @param amount
   * @return the formatted amount with the currency symbol and decimals.
   */
  fun format(amount: BigDecimal, currency: CurrencyModel): String
}

