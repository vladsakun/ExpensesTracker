package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.data.repository.api.CurrencyRatesRepository
import com.emendo.expensestracker.core.data.repository.api.CurrencyRepository
import javax.inject.Inject

class AppInitManagerImpl @Inject constructor(
  private val currencyRatesRepository: CurrencyRatesRepository,
  private val currencyRepository: CurrencyRepository,
) : AppInitManager {

  override suspend fun init() {
    val currencyCodes = currencyRatesRepository.populateCurrencyRatesIfEmpty()
    currencyRepository.initCurrenciesMap(currencyCodes)
  }
}