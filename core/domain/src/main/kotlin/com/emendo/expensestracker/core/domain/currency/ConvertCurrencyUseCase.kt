package com.emendo.expensestracker.core.domain.currency

import com.emendo.expensestracker.core.data.manager.CurrencyConverter
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import java.math.BigDecimal
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
  private val currencyConverter: CurrencyConverter,
) {

  operator fun invoke(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
  ): BigDecimal =
    currencyConverter.convert(
      value = value,
      fromCurrencyCode = fromCurrencyCode,
      toCurrencyCode = toCurrencyCode,
      currencyRates = currencyRateRepository.getRatesSnapshot(),
    )
}