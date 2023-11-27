package com.emendo.expensestracker.core.app.base.manager

import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import javax.inject.Inject

class AppInitManagerImpl @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
  private val currencyCacheManager: CurrencyCacheManager,
  private val createTransactionRepository: CreateTransactionRepository,
) : AppInitManager {

  override suspend fun init() {
    initCurrency()
    createTransactionRepository.init()
  }

  private suspend fun initCurrency() {
    currencyRateRepository.populateCurrencyRatesIfEmpty()
    currencyCacheManager.startCaching()
  }
}