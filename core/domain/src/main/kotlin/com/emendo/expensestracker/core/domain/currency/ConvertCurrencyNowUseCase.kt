package com.emendo.expensestracker.core.domain.currency

import com.emendo.expensestracker.core.model.data.exception.CurrencyRateNotFoundException
import com.emendo.expensestracker.data.api.manager.CurrencyConverter
import com.emendo.expensestracker.data.api.repository.CurrencyRateRepository
import java.math.BigDecimal
import javax.inject.Inject

class ConvertCurrencyNowUseCase @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
  private val currencyConverter: CurrencyConverter,
) {

  operator fun invoke(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
  ): BigDecimal =
    try {
      currencyConverter.convert(
        value = value,
        fromCurrencyCode = fromCurrencyCode,
        toCurrencyCode = toCurrencyCode,
        currencyRates = currencyRateRepository.getTodayRatesSnapshot(),
      )
    } catch (e: CurrencyRateNotFoundException) {
      value
    }
}