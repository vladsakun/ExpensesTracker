package com.emendo.expensestracker.core.network

import com.emendo.expensestracker.core.network.model.CurrencyRates

interface CurrencyRatesNetworkDataSource {
  suspend fun getCurrencyRates(): CurrencyRates
}