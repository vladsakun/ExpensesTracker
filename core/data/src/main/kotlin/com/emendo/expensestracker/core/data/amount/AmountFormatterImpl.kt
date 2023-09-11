package com.emendo.expensestracker.core.data.amount

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmountFormatterImpl @Inject constructor() : AmountFormatter {

  private val currencyLocale: Locale = Locale.getDefault()

  companion object {
    private const val MAX_DIGITS_BEFORE_DECIMAL = 11
    private const val MAX_DIGITS_AFTER_DECIMAL = 2
  }

  // If for some reason Default Locale changes (in phone settings, or from within the app -
  // shouldn't be), new instance of this class should be created.
  // To avoid the danger of not actual object, relocate this initialisation into the method
  // getFormattedValue, at the cost of lower performance.

  //we only use the currencynumberformatter and for instances where currency is not needed we remove it
  //this is because for instance de-at locale would result in 1.234,23 in currencyformatter
  // but 1 234,23 in regular formatter
  //and since we only format amounts we want it to be the same everywhere (with or without currency symbol)
  private val currencyNumberFormatter = NumberFormat.getCurrencyInstance(currencyLocale) as DecimalFormat
  private val isCurrencyFirst: Boolean

  override val decimalSeparator: Char
    get() = currencyNumberFormatter.decimalFormatSymbols.monetaryDecimalSeparator

  override val groupingSeparator: Char
    get() = currencyNumberFormatter.decimalFormatSymbols.monetaryGroupingSeparator

  override val maxDigitsAfterDecimal: Int = MAX_DIGITS_AFTER_DECIMAL
  override val maxDigitsBeforeDecimal: Int = MAX_DIGITS_BEFORE_DECIMAL

  init {
    currencyNumberFormatter.minimumFractionDigits = 2
    isCurrencyFirst = currencyNumberFormatter.toLocalizedPattern().indexOf('\u00A4') <= 0
  }

  override fun displayCurrencyFirst(): Boolean {
    return isCurrencyFirst
  }

  override fun format(amount: Amount?): String {
    return format(amount, true)
  }

  override fun format(amount: Amount?, includeDecimals: Boolean): String {
    return formatWithSpace(amount, includeDecimals).replace(' ', '\u00A0')
  }

  private fun formatWithSpace(amount: Amount?, includeDecimals: Boolean): String {
    if (amount == null) return ""

    // Use specific unicode character for minus and space https://issues.beeone.at/browse/GCUI-3748
    currencyNumberFormatter.negativePrefix = "\u2212\u00A0"

    currencyNumberFormatter.minimumFractionDigits = if (includeDecimals) amount.precision else 0
    val s = currencyNumberFormatter.format(amount.toBigDecimal())

    return removeCurrency(s)
  }

  /**
   * @param amount
   * @return the formatted amount value using the rules of number format of default Locale
   */
  override fun format(amount: BigDecimal): String {
    currencyNumberFormatter.minimumFractionDigits = amount.scale().coerceAtLeast(0)
    return removeCurrency(currencyNumberFormatter.format(amount))
  }

  override fun format(amount: String): String {
    return format(toAmount(amount))
  }

  override fun toAmount(from: String): Amount {
    val fromTrimmed = from.trim()

    if (fromTrimmed.isEmpty()) {
      return Amount(0, 0)
    }

    val precision = fromTrimmed.substringAfterLast(decimalSeparator, "").length
    val value = fromTrimmed.replace(groupingSeparator.toString(), "").replace(decimalSeparator.toString(), "").toLong()

    return Amount(
      value = value,
      precision = precision,
    )
  }

  override fun containsDecimalSeparator(value: StringBuilder?) = value?.contains(decimalSeparator) ?: false

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