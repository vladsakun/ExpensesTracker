package com.emendo.expensestracker.core.app.base.shared.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.model.data.CurrencyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SelectCurrencyViewModel @Inject constructor(
  currencyCacheManager: CurrencyCacheManager,
) : ViewModel() {

  val state: StateFlow<ImmutableList<CurrencyModel>?> =
    getCurrencyModelsState(currencyCacheManager)
      .stateInWhileSubscribed(
        viewModelScope,
        currencyCacheManager.getCurrenciesMapSnapshot()?.values?.toImmutableList()
      )
}

private fun getCurrencyModelsState(currencyCacheManager: CurrencyCacheManager): Flow<ImmutableList<CurrencyModel>?> =
  currencyCacheManager
    .currencyCodes
    .map { it?.values?.toImmutableList() }