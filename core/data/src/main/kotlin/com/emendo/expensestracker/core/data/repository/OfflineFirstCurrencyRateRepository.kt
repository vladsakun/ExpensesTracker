package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInLazily
import com.emendo.expensestracker.core.app.common.ext.stateInLazilyList
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.mapper.toCurrencyRateEntity
import com.emendo.expensestracker.core.data.mapper.toCurrencyRateModel
import com.emendo.expensestracker.core.data.suspendRunCatching
import com.emendo.expensestracker.core.database.dao.CurrencyRateDao
import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.core.datastore.ExpePreferencesDataStore
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import com.emendo.expensestracker.data.api.Synchronizer
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import com.emendo.expensestracker.data.api.repository.CurrencyRateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import javax.inject.Inject

class OfflineFirstCurrencyRateRepository @Inject constructor(
  private val network: CurrencyRatesNetworkDataSource,
  private val currencyRatesDao: CurrencyRateDao,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val expePreferencesDataStore: ExpePreferencesDataStore,
  @ApplicationScope private val scope: CoroutineScope,
) : CurrencyRateRepository {

  private val ratesStateFlow: StateFlow<Map<String, CurrencyRateModel>> =
    currencyRatesDao
      .getAll()
      .map { currencyRates ->
        currencyRates
          .mapNotNull(::toCurrencyRateModel)
          .associateBy { it.currencyCode }
      }
      .stateInLazily(scope, emptyMap())

  private val currencies: StateFlow<List<String>> =
    currencyRatesDao
      .getCurrencyCodes()
      .stateInLazilyList(scope)

  override fun getRates(): Flow<Map<String, CurrencyRateModel>> = ratesStateFlow

  override fun getCurrencyCodes(): Flow<List<String>> = currencies

  override fun getRatesSnapshot(): Map<String, CurrencyRateModel> =
    ratesStateFlow.value

  override suspend fun retrieveAllCurrencyCodes(): List<String> = withContext(ioDispatcher) {
    currencyRatesDao.retrieveAllCurrencyCodes()
  }

  override suspend fun populateCurrencyRatesIfEmpty() {
    if (expePreferencesDataStore.getChangeListVersions().currencyRatesLastUpdateInstant != null) {
      return
    }

    populateCurrencyRates()
  }

  override suspend fun populateCurrencyRates() {
    withContext(ioDispatcher) {
      currencyRatesDao.save(fetchCurrencyRateEntities())
      expePreferencesDataStore.updateChangeListVersion {
        copy(currencyRatesLastUpdateInstant = Clock.System.now())
      }
    }
  }

  override suspend fun syncWith(synchronizer: Synchronizer): Boolean = withContext(ioDispatcher) {
    suspendRunCatching {
      with(synchronizer) {
        val currentVersion = getChangeListVersions().currencyRatesLastUpdateInstant
        if (currentVersion != null && currentVersion >= Clock.System.now().minus(24, DateTimeUnit.HOUR)) {
          return@suspendRunCatching true
        }

        val currencyRateEntities = fetchCurrencyRateEntities()
        currencyRatesDao.save(currencyRateEntities)

        updateChangeListVersions {
          copy(currencyRatesLastUpdateInstant = Clock.System.now())
        }
      }
    }.isSuccess
  }

  override suspend fun deleteAll() {
    currencyRatesDao.deleteAll()
  }

  private suspend fun fetchCurrencyRateEntities(): List<CurrencyRateEntity> =
    network.getCurrencyRates().rates.map(::toCurrencyRateEntity)
}

