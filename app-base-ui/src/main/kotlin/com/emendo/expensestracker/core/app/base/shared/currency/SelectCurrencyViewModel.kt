package com.emendo.expensestracker.core.app.base.shared.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.manager.CurrencyCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SelectCurrencyViewModel @Inject constructor(
  currencyCacheManager: CurrencyCacheManager,
) : ViewModel() {

  private val allCurrencies: StateFlow<ImmutableList<CurrencyModel>?> =
    getCurrencyModelsState(currencyCacheManager)
      .stateInWhileSubscribed(
        viewModelScope,
        currencyCacheManager.getCurrenciesMapSnapshot()?.values?.toImmutableList(),
      )

  private val _searchQuery = MutableStateFlow("")
  internal val searchQuery: StateFlow<String> = _searchQuery

  internal val filteredCurrencies: StateFlow<ImmutableList<CurrencyModel>?> =
    combine(allCurrencies, _searchQuery) { currencies, query ->
      if (currencies == null) {
        return@combine null
      }
      if (query.isBlank()) {
        return@combine currencies
      }

      return@combine currencies.filter {
        it.currencyCode.contains(query, ignoreCase = true) ||
          it.currencyName.contains(query, ignoreCase = true) ||
          (it.currencySymbol?.contains(query, ignoreCase = true) == true)
      }.toImmutableList()
    }.stateInWhileSubscribed(viewModelScope, null)

  internal fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }
}

private fun getCurrencyModelsState(currencyCacheManager: CurrencyCacheManager): Flow<ImmutableList<CurrencyModel>?> =
  currencyCacheManager
    .currencyModels
    .map { it?.values?.toImmutableList() }