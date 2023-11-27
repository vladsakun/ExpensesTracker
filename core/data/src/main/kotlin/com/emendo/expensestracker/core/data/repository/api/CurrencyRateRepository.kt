package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.data.Syncable
import com.emendo.expensestracker.core.data.model.CurrencyRateModel
import kotlinx.coroutines.flow.Flow

interface CurrencyRateRepository : Syncable {
  val rates: Flow<List<CurrencyRateModel>>
  val currencyCodes: Flow<List<String>>
  suspend fun retrieveAllCurrencyCodes(): List<String>
  suspend fun populateCurrencyRatesIfEmpty()
  suspend fun populateCurrencyRates()
  suspend fun deleteAll()
}