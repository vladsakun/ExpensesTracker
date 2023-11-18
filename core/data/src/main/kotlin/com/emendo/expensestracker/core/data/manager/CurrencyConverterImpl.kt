package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.app.base.exception.CurrencyRateNotFoundException
import com.emendo.expensestracker.core.data.applyDefaultDecimalStyle
import com.emendo.expensestracker.core.data.model.CurrencyRateModel
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import com.emendo.expensestracker.core.model.data.CurrencyModels
import java.math.BigDecimal
import javax.inject.Inject

class CurrencyConverterImpl @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
) : CurrencyConverter {

  override suspend fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    currencyRates: List<CurrencyRateModel>,
  ): BigDecimal {
    if (fromCurrencyCode == toCurrencyCode) {
      return value
    }

    val toRate = currencyRates.find { it.currencyCode == toCurrencyCode }?.rate ?: throw CurrencyRateNotFoundException(
      currencyRateRepository::populateCurrencyRates
    )
    if (fromCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return value * toRate
    }

    val fromRate =
      currencyRates.find { it.currencyCode == fromCurrencyCode }?.rate ?: throw CurrencyRateNotFoundException(
        currencyRateRepository::populateCurrencyRates
      )
    if (toCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return value / fromRate
    }

    return (value / fromRate * toRate).applyDefaultDecimalStyle()
  }
}