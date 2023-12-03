package com.emendo.expensestracker.core.data.amount

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import com.emendo.expensestracker.core.data.isFloatingPointNumber
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManager
import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

class AmountFormatterImpl @Inject constructor(
  private val localeManager: ExpeLocaleManager,
  private val calculatorFormatter: CalculatorFormatter,
) : AmountFormatter {

  private val currentLocale: Locale
    get() = localeManager.getLocale()

  private var formatterWithLocale = currentLocale to formatter

  private val currencyNumberFormatter: DecimalFormat
    get() {
      val locale = currentLocale
      if (formatterWithLocale.first != locale) {
        formatterWithLocale = currentLocale to formatter
      }

      return formatterWithLocale.second
    }

  private val formatter: DecimalFormat
    get() = (NumberFormat.getCurrencyInstance(currentLocale) as DecimalFormat).apply {
      minimumFractionDigits = 2
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
        minimumFractionDigits = 0
      }

      decimalFormatSymbols = decimalFormatSymbols.apply {
        currencySymbol = currency.currencySymbolOrCode
      }
    }

    return currencyNumberFormatter.format(amount)
  }

  override fun replaceCurrency(s: String, oldCurrency: CurrencyModel, newCurrencyModel: CurrencyModel): String {
    val noCurrencyAmount = removeCurrency(s, oldCurrency)
    return format(calculatorFormatter.toBigDecimal(noCurrencyAmount), newCurrencyModel)
  }

  //removes currency symbol and possible space between currency and amount
  override fun removeCurrency(s: String, currency: CurrencyModel): String {
    var symbol = s
    //currency sign
    symbol = symbol.replace(currency.currencySymbolOrCode, "")
    //possible leading non breaking space
    symbol = symbol.replace("^\u00A0+".toRegex(), "")
    //possible trailing non breaking space
    symbol = symbol.replace("\u00A0+$".toRegex(), "")

    return symbol
  }
}