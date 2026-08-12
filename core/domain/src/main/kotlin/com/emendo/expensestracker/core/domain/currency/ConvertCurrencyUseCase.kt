package com.emendo.expensestracker.core.domain.currency

import com.emendo.expensestracker.data.api.manager.CurrencyConverter
import com.emendo.expensestracker.data.api.repository.CurrencyRateRepository
import kotlinx.datetime.Instant
import java.math.BigDecimal
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
  private val currencyConverter: CurrencyConverter,
) {

  suspend operator fun invoke(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    usdToOriginalRate: BigDecimal,
    conversionDate: Instant,
  ): BigDecimal = value
  // TODO remove Use API to fetch real currency rates
  //    try {
  //      currencyConverter.convert(
  //        value = value,
  //        fromCurrencyCode = fromCurrencyCode,
  //        toCurrencyCode = toCurrencyCode,
  //        usdToOriginalRate = usdToOriginalRate,
  //        currencyRates = currencyRateRepository.getOrFetchRates(conversionDate),
  //      )
  //    } catch (e: CurrencyRateNotFoundException) {
  //      value
  //    }
}