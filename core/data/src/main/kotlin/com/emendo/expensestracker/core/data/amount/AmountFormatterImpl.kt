package com.emendo.expensestracker.core.data.amount

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import com.emendo.expensestracker.core.data.isFloatingPointNumber
import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// Todo refactor

@Singleton
class AmountFormatterImpl @Inject constructor() : AmountFormatter {

  private val currencyLocale: Locale = Locale.getDefault()

  companion object {
    private const val MAX_DIGITS_BEFORE_DECIMAL = 11
    private const val MAX_DIGITS_AFTER_DECIMAL = 2
    private const val NEGATIVE_PREFIX = "\u2212\u00A0"
    private const val SPACE = '\u00A0'
  }

  // If for some reason Default Locale changes (in phone settings, or from within the app -
  // shouldn't be), new instance of this class should be created.
  // To avoid the danger of not actual object, relocate this initialisation into the method
  // getFormattedValue, at the cost of lower performance.

  //we only use the currencynumberformatter and for instances where currency is not needed we remove it
  //this is because for instance de-at locale would result in 1.234,23 in currencyformatter
  // but 1 234,23 in regular formatter
  //and since we only format amounts we want it to be the same everywhere (with or without currency symbol)
  private val currencyNumberFormatter = (NumberFormat.getCurrencyInstance(currencyLocale) as DecimalFormat).apply {
    // Use specific unicode character for minus and space
    negativePrefix = NEGATIVE_PREFIX
  }
  private val numberFormatter = (NumberFormat.getInstance(currencyLocale) as DecimalFormat).apply {
    negativePrefix = NEGATIVE_PREFIX
  }

  override val decimalSeparator: Char
    get() = currencyNumberFormatter.decimalFormatSymbols.monetaryDecimalSeparator

  override val groupingSeparator: Char
    get() = currencyNumberFormatter.decimalFormatSymbols.monetaryGroupingSeparator

  override val maxDigitsAfterDecimal: Int = MAX_DIGITS_AFTER_DECIMAL
  override val maxDigitsBeforeDecimal: Int = MAX_DIGITS_BEFORE_DECIMAL

  init {
    currencyNumberFormatter.minimumFractionDigits = 2
    numberFormatter.minimumFractionDigits = 2
  }

  /**
   * @param amount
   * @return the formatted amount value using the rules of number format of default Locale
   */
  override fun format(amount: BigDecimal, currency: CurrencyModel): String {
    currencyNumberFormatter.apply {
      if (amount.isFloatingPointNumber) {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
      } else {
        maximumFractionDigits = 0
      }

      decimalFormatSymbols = decimalFormatSymbols.apply {
        currencySymbol = currency.currencySymbolOrCode
      }
    }

    return currencyNumberFormatter.format(amount)
  }

  override fun format(amount: BigDecimal?): String {
    return formatWithSpace(amount).replace(' ', SPACE)
  }

  override fun format(amount: String): String {
    return format(toBigDecimal(amount))
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

  override fun toBigDecimal(from: String): BigDecimal {
    val fromTrimmed = from.trim().replace(SPACE.toString(), "").replace('−', '-')

    if (fromTrimmed.isEmpty()) {
      return BigDecimal.ZERO
    }

    val precision = fromTrimmed.substringAfterLast(decimalSeparator, "").length
    val value = fromTrimmed.replace(groupingSeparator.toString(), "").replace(decimalSeparator.toString(), "").toLong()

    return BigDecimal(value.toBigInteger(), precision)
  }

  private fun formatWithSpace(amount: BigDecimal?): String {
    if (amount == null) {
      return ""
    }

    numberFormatter.minimumFractionDigits = amount.scale()

    return numberFormatter.format(amount)
  }

  //removes currency symbol and possible space between currency and amount
  private fun removeCurrency(s: String): String {
    var symbol = s
    //currency sign
    symbol = symbol.replace(currencyNumberFormatter.decimalFormatSymbols.currencySymbol, "")
    //possible leading non breaking space
    symbol = symbol.replace("^\u00A0+".toRegex(), "")
    //possible trailing non breaking space
    symbol = symbol.replace("\u00A0+$".toRegex(), "")


    return symbol
  }
}