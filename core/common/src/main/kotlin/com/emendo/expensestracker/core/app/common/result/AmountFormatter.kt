package com.emendo.expensestracker.core.app.common.result

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.icu.util.Currency
import timber.log.Timber
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

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

  fun format(amount: Amount?, includeCurrency: Boolean = true, includeDecimals: Boolean = true): String

  /**
   * @param amount
   * @return the formatted amount without the currency symbol
   */
  fun format(amount: BigDecimal): String

  /**
   * @return true if currency should be displayed before amount.
   */
  fun displayCurrencyFirst(): Boolean

  /**
   * @return formatted amount for accessibility
   */
  fun amountForAccessibility(amount: Amount?): String

  /**
   * @param currencyCode ISO 4217 currency code
   * @return the symbol of the currency or the currency code if the symbol is not available or
   * is not a supported ISO 4217 currency code
   */
  fun getCurrencySymbol(currencyCode: String): String

  fun toAmount(from: String, currency: String): Amount {
    val fromTrimmed = from.trim()

    if (fromTrimmed.isEmpty()) return Amount(0, 0, currency)

    val minPrecision = if (containsDecimal(fromTrimmed)) 1 else 0

    val precision = fromTrimmed.substringAfterLast(decimalSeparator, "").length
    val value = fromTrimmed.replace(groupingSeparator.toString(), "").replace(decimalSeparator.toString(), "").toLong()

    return Amount(
      value = value,
      precision = precision,
      currency = currency,
    )
  }

  fun containsDecimal(value: StringBuilder?) = value?.contains(decimalSeparator) ?: false
  fun containsDecimal(value: String?) = value?.contains(decimalSeparator) ?: false

}

class AmountFormatterImpl @Inject constructor() : AmountFormatter {

  private val useIsoCurrency: Boolean = false
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
    return format(amount, true, true)
  }

  override fun format(amount: Amount?, includeCurrency: Boolean, includeDecimals: Boolean): String {
    return formatWithSpace(amount, includeCurrency, includeDecimals).replace(' ', '\u00A0')
  }

  private fun formatWithSpace(amount: Amount?, includeCurrency: Boolean, includeDecimals: Boolean): String {
    if (amount == null) return ""

    val currency = if (includeCurrency) getCurrency(amount.currency) else null
    currency?.let { currencyNumberFormatter.currency = it }

    // Use specific unicode character for minus and space https://issues.beeone.at/browse/GCUI-3748
    currencyNumberFormatter.negativePrefix = "\u2212\u00A0"

    currencyNumberFormatter.minimumFractionDigits = if (includeDecimals) amount.precision else 0
    val s = currencyNumberFormatter.format(amount.toBigDecimal())

    if (currency == null && includeCurrency || useIsoCurrency) {
      return removeCurrency(s) + " " + amount.currency
    }

    return if (includeCurrency) s else removeCurrency(s)
  }

  /**
   * @param amount
   * @return the formatted amount value using the rules of number format of default Locale
   */
  override fun format(amount: BigDecimal): String {
    currencyNumberFormatter.minimumFractionDigits = amount.scale().coerceAtLeast(0)
    return removeCurrency(currencyNumberFormatter.format(amount))
  }

  private fun getCurrency(currency: String): Currency? {
    try {
      return Currency.getInstance(currency)
    } catch (e: IllegalArgumentException) {
      Timber.e(e)
      return null
    }
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

  override fun getCurrencySymbol(currencyCode: String): String {
    val currency = getCurrency(currencyCode)
    return if (currency != null)
      currency.getSymbol(currencyLocale)
    else
      currencyCode
  }

  override fun amountForAccessibility(amount: Amount?): String {
    if (amount != null) {
      val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        this.currency = getCurrency(amount.currency)
      }
      val currency = if (amount.currency == "RON") {
        Currency.getInstance(amount.currency).displayName
      } else {
        amount.currency
      }
      val formattedAmount = numberFormatter.format(amount.toBigDecimal())
      return if (currency.isBlank()) {
        formattedAmount
      } else {
        "$formattedAmount $currency"
      }
    } else return ""
  }
}
