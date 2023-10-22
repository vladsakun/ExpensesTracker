package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.data.Syncable
import com.emendo.expensestracker.core.data.repository.CurrencyRateModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface CurrencyRatesRepository : Syncable {
  fun getRates(): Flow<List<CurrencyRateModel>>
  suspend fun retrieveAllCurrencyCodes(): List<String>
  suspend fun getRate(currencyCode: String): BigDecimal
  suspend fun populateCurrencyRatesIfEmpty(): List<String>
}