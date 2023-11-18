package com.emendo.expensestracker.core.data.manager.cache

import com.emendo.expensestracker.core.model.data.CurrencyModel

interface CurrencyCacheManager {
  fun startCaching()
  fun getCurrenciesBlocking(): Map<String, CurrencyModel>
  fun getGeneralCurrencySnapshot(): CurrencyModel
}