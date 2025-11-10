package com.emendo.expensestracker.data.api.repository

import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import com.emendo.expensestracker.data.api.utils.Syncable
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import java.math.BigDecimal

interface CurrencyRateRepository : Syncable {
  //  fun getRates(): Flow<Map<String, CurrencyRateModel>>
  fun getCurrencyCodes(): Flow<List<String>>

  fun getTodayRatesSnapshot(): Map<String, CurrencyRateModel>
  suspend fun getOrFetchRate(targetCode: String, date: Instant): BigDecimal
  suspend fun getOrFetchRates(date: Instant): Map<String, CurrencyRateModel>

  suspend fun populateCurrencyRatesIfEmpty()
  suspend fun populateCurrencyRates()

  suspend fun deleteAll()
}