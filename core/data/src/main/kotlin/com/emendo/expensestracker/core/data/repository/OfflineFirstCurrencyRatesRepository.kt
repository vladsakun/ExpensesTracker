package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.repository.api.CurrencyRatesRepository
import java.math.BigDecimal
import javax.inject.Inject

class OfflineFirstCurrencyRatesRepository @Inject constructor() : CurrencyRatesRepository {
  override fun getRates(): Map<String, BigDecimal> {
    // Todo Get from database
    val toUsdRates = mapOf(
      "EUR" to BigDecimal(1.06),
      "GBP" to BigDecimal(1.23),
      "JPY" to BigDecimal(0.0067),
      "RUB" to BigDecimal(0.010),
    )
    return toUsdRates
  }

  override fun saveRates(rates: Map<String, BigDecimal>) {
    TODO("Not yet implemented")
  }
}