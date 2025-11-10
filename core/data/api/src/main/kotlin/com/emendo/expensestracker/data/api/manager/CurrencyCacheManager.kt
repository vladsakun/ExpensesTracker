package com.emendo.expensestracker.data.api.manager

import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import kotlinx.coroutines.flow.StateFlow

interface CurrencyCacheManager {
  val currencyModels: StateFlow<Map<String, CurrencyModel>?>
  fun getCurrenciesMapSnapshot(): Map<String, CurrencyModel>?
  fun getGeneralCurrencySnapshot(): CurrencyModel
}