package com.emendo.expensestracker.core.data.manager.cache

import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.flow.StateFlow

interface CurrencyCacheManager {
  val currencyCodes: StateFlow<Map<String, CurrencyModel>?>
  fun startCaching()
  fun getCurrenciesMapSnapshot(): Map<String, CurrencyModel>?
  fun getGeneralCurrencySnapshot(): CurrencyModel
}