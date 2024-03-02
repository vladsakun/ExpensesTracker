package com.emendo.expensestracker.core.data.amount

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import com.emendo.expensestracker.core.data.isFloatingPointNumber
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

class AmountFormatterImpl @Inject constructor(
  private val localeManager: ExpeLocaleManager,
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
  override fun format(amount: BigDecimal, currency: CurrencyModel): Amount {
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

    return Amount(
      formattedValue = currencyNumberFormatter.format(amount),
      currency = currency,
      value = amount,
    )
  }

  override fun replaceCurrency(amount: Amount, newCurrencyModel: CurrencyModel): Amount {
    return format(amount.value, newCurrencyModel)
  }
}