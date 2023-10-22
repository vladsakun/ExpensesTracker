package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
  val currenciesList: Map<String, CurrencyModel>
  val generalCurrency: Flow<CurrencyModel>
  val favouriteCurrencies: Flow<List<CurrencyModel>>

  suspend fun markAsFavourite(currencyModel: CurrencyModel)
  fun getLastGeneralCurrency(): CurrencyModel?
  fun findCurrencyModel(currencyCode: String): CurrencyModel
  fun initCurrenciesMap(supportedCurrencyCodes: List<String>)
}