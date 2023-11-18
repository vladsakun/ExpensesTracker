package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.data.Synchronizer
import com.emendo.expensestracker.core.data.model.CurrencyRateModel
import com.emendo.expensestracker.core.data.model.toCurrencyRateEntity
import com.emendo.expensestracker.core.data.model.toCurrencyRateModel
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import com.emendo.expensestracker.core.data.suspendRunCatching
import com.emendo.expensestracker.core.database.dao.CurrencyRateDao
import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.core.datastore.ExpePreferencesDataStore
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
) : CurrencyRateRepository {

  override fun getRates(): Flow<List<CurrencyRateModel>> =
    currencyRatesDao.getAll().map {
      it.mapNotNull(::toCurrencyRateModel)
    }

  override fun getCurrencyCodes(): Flow<List<String>> =
    currencyRatesDao.getCurrencyCodes()

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

  private suspend fun fetchCurrencyRateEntities(): List<CurrencyRateEntity> {
    return network.getCurrencyRates().rates.map(::toCurrencyRateEntity)
  }
}

