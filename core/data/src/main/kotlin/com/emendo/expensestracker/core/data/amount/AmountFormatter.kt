package com.emendo.expensestracker.core.data.amount

import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

// Todo refactor

interface AmountFormatter {

  /**
   * @return max of digits before decimal part of amount
   */
  val maxDigitsBeforeDecimal: Int

  /**
   * @return max of digits after decimal part of amount
   */
  val maxDigitsAfterDecimal: Int

  /**
   * @return group separator (e.g thousand sign)
   */
  val groupingSeparator: Char

  /**
   * @return decimal separator
   */
  val decimalSeparator: Char

  /**
   * @param amount
   * @return the formatted amount with the currency symbol and decimals.
   */
  fun format(amount: BigDecimal, currency: CurrencyModel): String

  /**
   * @param amount
   * @return the formatted amount with decimals and without the currency symbol.
   */
  fun format(amount: BigDecimal?): String

  /**
   * @param amount
   * @return the formatted amount with decimals and without the currency symbol.
   */
  fun formatFinal(amount: BigDecimal?): String

  /**
   * @param amount
   * @return the formatted amount with decimals and without the currency symbol.
   */
  fun format(amount: String): String

  fun toBigDecimal(from: String): BigDecimal
}

