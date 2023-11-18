package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import javax.inject.Inject

class AppInitManagerImpl @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
  private val currencyCacheManager: CurrencyCacheManager,
) : AppInitManager {

  override suspend fun init() {
    initCurrency()
  }

  private suspend fun initCurrency() {
    currencyRateRepository.populateCurrencyRatesIfEmpty()
    currencyCacheManager.startCaching()
  }
}