package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.data.applyDefaultDecimalStyle
import com.emendo.expensestracker.core.data.divideWithScale
import com.emendo.expensestracker.core.model.data.currency.CurrencyModels
import com.emendo.expensestracker.core.model.data.exception.CurrencyRateNotFoundException
import com.emendo.expensestracker.data.api.manager.CurrencyConverter
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import java.math.BigDecimal
import javax.inject.Inject

class CurrencyConverterImpl @Inject constructor() : CurrencyConverter {

  override fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    currencyRates: Map<String, CurrencyRateModel>,
  ): BigDecimal {
    if (fromCurrencyCode == toCurrencyCode) {
      return value
    }

    val toRate = currencyRates[toCurrencyCode]?.rate ?: throw CurrencyRateNotFoundException()
    if (fromCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return (value * toRate).applyDefaultDecimalStyle()
    }

    val fromRate = currencyRates[fromCurrencyCode]?.rate ?: throw CurrencyRateNotFoundException()
    if (toCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return value.divideWithScale(fromRate)
    }

    return value.divideWithScale(fromRate).multiply(toRate).applyDefaultDecimalStyle()
  }
}