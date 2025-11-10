package com.emendo.expensestracker.core.network

import com.emendo.expensestracker.core.network.model.CurrencyRates
import kotlinx.datetime.Month
import java.time.Year

interface CurrencyRatesNetworkDataSource {
  suspend fun getCurrencyRates(): CurrencyRates
  suspend fun getCurrencyRatesDate(year: Year, month: Month, day: String): CurrencyRates
}