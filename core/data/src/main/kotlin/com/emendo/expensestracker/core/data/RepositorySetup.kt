package com.emendo.expensestracker.core.data

import com.emendo.expensestracker.core.android.api.OnAppCreate
import com.emendo.expensestracker.data.api.repository.CurrencyRateRepository
import javax.inject.Inject

class RepositorySetup @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
) : OnAppCreate {

  override suspend fun onCreate() {
    currencyRateRepository.populateCurrencyRatesIfEmpty()
  }
}