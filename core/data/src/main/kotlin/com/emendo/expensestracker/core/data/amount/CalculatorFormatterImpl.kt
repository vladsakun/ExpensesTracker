package com.emendo.expensestracker.core.data.amount

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import com.emendo.expensestracker.core.data.isFloatingPointNumber
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManager
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

class CalculatorFormatterImpl @Inject constructor(
  private val localeManager: ExpeLocaleManager,
) : CalculatorFormatter {

  companion object {
    private const val MAX_DIGITS_BEFORE_DECIMAL = 11
    private const val MAX_DIGITS_AFTER_DECIMAL = 2
    private const val SPACE = '\u00A0'
  }

  override val decimalSeparator: Char
    get() = numberFormatter.decimalFormatSymbols.monetaryDecimalSeparator

  override val groupingSeparator: Char
    get() = numberFormatter.decimalFormatSymbols.monetaryGroupingSeparator

  override val maxDigitsAfterDecimal: Int = MAX_DIGITS_AFTER_DECIMAL
  override val maxDigitsBeforeDecimal: Int = MAX_DIGITS_BEFORE_DECIMAL

  private val currentLocale: Locale
    get() = localeManager.getLocale()

  private var formatterWithLocale = currentLocale to formatter

  private val numberFormatter: DecimalFormat
    get() {
      val locale = currentLocale
      if (formatterWithLocale.first != locale) {
        formatterWithLocale = currentLocale to formatter
      }

      return formatterWithLocale.second
    }

  private val formatter: DecimalFormat
    get() = (NumberFormat.getInstance(currentLocale) as DecimalFormat).apply {
      minimumFractionDigits = 2
    }

  override fun formatFinal(amount: BigDecimal?): String {
    if (amount == null) {
      return ""
    }

    if (amount.scale() != 0) {
      numberFormatter.minimumFractionDigits = amount.scale().coerceAtLeast(2)
    } else {
      numberFormatter.minimumFractionDigits = 0
    }

    return numberFormatter.format(amount)
  }

  override fun formatFinalWithPrecision(amount: BigDecimal?): String {
    if (amount == null) {
      return ""
    }

    if (amount.isFloatingPointNumber) {
      numberFormatter.minimumFractionDigits = amount.scale().coerceAtLeast(2)
    } else {
      numberFormatter.minimumFractionDigits = 0
    }

    return numberFormatter.format(amount)
  }

  override fun format(amount: String): String {
    return formatBigDecimal(toBigDecimal(amount))
  }

  override fun toBigDecimal(from: String): BigDecimal {
    val fromTrimmed = from.trim().replace(SPACE.toString(), "").replace('−', '-')

    if (fromTrimmed.isEmpty()) {
      return BigDecimal.ZERO
    }

    val precision = fromTrimmed.substringAfterLast(decimalSeparator, "").length
    val value =
      fromTrimmed.replace(groupingSeparator.toString(), "").replace(decimalSeparator.toString(), "").toBigInteger()

    return BigDecimal(value, precision)
  }

  private fun formatBigDecimal(amount: BigDecimal?): String {
    return formatWithSpace(amount).replace(' ', SPACE)
  }

  private fun formatWithSpace(amount: BigDecimal?): String {
    if (amount == null) {
      return ""
    }

    numberFormatter.minimumFractionDigits = amount.scale()

    return numberFormatter.format(amount)
  }
}