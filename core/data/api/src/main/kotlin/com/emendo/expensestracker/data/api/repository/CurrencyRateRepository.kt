package com.emendo.expensestracker.data.api.repository

import com.emendo.expensestracker.data.api.Syncable
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import kotlinx.coroutines.flow.Flow

interface CurrencyRateRepository : Syncable {
  fun getRates(): Flow<Map<String, CurrencyRateModel>>
  fun getCurrencyCodes(): Flow<List<String>>

  fun getRatesSnapshot(): Map<String, CurrencyRateModel>

  suspend fun retrieveAllCurrencyCodes(): List<String>
  suspend fun populateCurrencyRatesIfEmpty()
  suspend fun populateCurrencyRates()

  suspend fun deleteAll()
}