package com.emendo.expensestracker.core.data.repository.api

import java.math.BigDecimal

interface CurrencyRatesRepository {
  fun getRates(): Map<String, BigDecimal>
  fun saveRates(rates: Map<String, BigDecimal>)
}