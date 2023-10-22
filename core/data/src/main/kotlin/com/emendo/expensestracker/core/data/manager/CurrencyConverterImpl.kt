package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.data.applyDefaultDecimalStyle
import com.emendo.expensestracker.core.data.repository.api.CurrencyRatesRepository
import com.emendo.expensestracker.core.model.data.CurrencyModels
import java.math.BigDecimal
import javax.inject.Inject

class CurrencyConverterImpl @Inject constructor(
  private val currencyRatesRepository: CurrencyRatesRepository,
) : CurrencyConverter {

  override suspend fun convert(value: BigDecimal, fromCurrencyCode: String, toCurrencyCode: String): BigDecimal {
    if (fromCurrencyCode == toCurrencyCode) {
      return value
    }

    val toRate = currencyRatesRepository.getRate(toCurrencyCode)
    if (fromCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return value * toRate
    }

    val fromRate = currencyRatesRepository.getRate(fromCurrencyCode)
    if (toCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return value / fromRate
    }

    return (value / fromRate * toRate).applyDefaultDecimalStyle()
  }
}