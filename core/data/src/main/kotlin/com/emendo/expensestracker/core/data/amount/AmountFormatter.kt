package com.emendo.expensestracker.core.data.amount

import java.math.BigDecimal

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
   * @return the formatted amount along with the currency symbol and decimals (optional).
   */
  fun format(amount: Amount?): String

  fun format(amount: Amount?, includeDecimals: Boolean = true): String

  /**
   * @param amount
   * @return the formatted amount without the currency symbol
   */
  fun format(amount: BigDecimal): String

  fun format(amount: String): String

  /**
   * @return true if currency should be displayed before amount.
   */
  fun displayCurrencyFirst(): Boolean

  fun toAmount(from: String): Amount

  fun containsDecimalSeparator(value: StringBuilder?): Boolean
}

