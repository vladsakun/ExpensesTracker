package com.emendo.expensestracker.core.app.base.manager

import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import javax.inject.Inject

class AppInitManagerImpl @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
) : AppInitManager {

  override suspend fun init() {
    initCurrency()
  }

  private suspend fun initCurrency() {
    currencyRateRepository.populateCurrencyRatesIfEmpty()
  }
}