package com.emendo.expensestracker.data.api.amount

import java.math.BigDecimal

interface CalculatorFormatter {

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
   * @return the formatted amount with decimals and without the currency symbol.
   */
  fun format(amount: String): String

  /**
   * @param amount
   * @return the formatted amount with decimals and without the currency symbol.
   */
  fun formatFinal(amount: BigDecimal?): String

  /**
   * @param amount
   * @return the formatted amount with decimals and without the currency symbol.
   */
  fun formatFinalWithPrecision(amount: BigDecimal?): String

  /**
   * @param amount
   * @return the formatted amount with decimals and without the currency symbol.
   */
  fun formatFinalWithMax2Precision(amount: BigDecimal?): String

  fun toBigDecimal(from: String): BigDecimal
}

