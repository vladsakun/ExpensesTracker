package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.data.applyDefaultDecimalStyle
import com.emendo.expensestracker.core.data.repository.api.CurrencyRatesRepository
import java.math.BigDecimal
import javax.inject.Inject

class CurrencyConverterImpl @Inject constructor(
  private val currencyRatesRepository: CurrencyRatesRepository,
) : CurrencyConverter {
  override fun convert(value: BigDecimal, fromCurrencyCode: String, toCurrencyCode: String): BigDecimal {
    if (toCurrencyCode == fromCurrencyCode) {
      return value
    }

    val toUSDRates = currencyRatesRepository.getRates()

    val toUSDRate =
      toUSDRates[fromCurrencyCode] ?: throw IllegalArgumentException("Unknown currency code: $fromCurrencyCode")

    if (toCurrencyCode == "USD") {
      return value / toUSDRate
    }

    val fromUSDRate =
      toUSDRates[toCurrencyCode] ?: throw IllegalArgumentException("Unknown currency code: $toCurrencyCode")
    val toUSDValue = value * toUSDRate
    return (toUSDValue / fromUSDRate).applyDefaultDecimalStyle()
  }
}