package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInEagerly
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
import com.emendo.expensestracker.core.model.data.exception.CurrencyRateNotFoundException
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import com.emendo.expensestracker.data.api.repository.CurrencyRateRepository
import com.emendo.expensestracker.data.api.utils.Synchronizer
import com.emendo.expensestracker.data.api.utils.instantToDateString
import com.emendo.expensestracker.data.api.utils.todayAsString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import timber.log.Timber
import java.math.BigDecimal
import java.time.Year
import javax.inject.Inject

class OfflineFirstCurrencyRateRepository @Inject constructor(
  private val network: CurrencyRatesNetworkDataSource,
  private val currencyRatesDao: CurrencyRateDao,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val expePreferencesDataStore: ExpePreferencesDataStore,
  @ApplicationScope private val scope: CoroutineScope,
) : CurrencyRateRepository {

  private val todayRatesStateFlow: StateFlow<Map<String, CurrencyRateModel>> =
    currencyRatesDao
      .getAll()
      .map { currencyRates ->
        currencyRates
          .filter { it.rateDate == todayAsString() }
          .map(::toCurrencyRateModel)
          .associateBy { it.currencyCode }
      }
      .stateInEagerly(scope, emptyMap())

  private val currencies: StateFlow<List<String>> =
    currencyRatesDao
      .getCurrencyCodes()
      .stateInLazilyList(scope)

  // Date to List of rates
  val ratesCache = mutableMapOf<String, MutableMap<String, CurrencyRateEntity>>()

  //  override fun getRates(): Flow<Map<String, CurrencyRateModel>> = ratesStateFlow

  override fun getCurrencyCodes(): Flow<List<String>> = currencies

  override fun getTodayRatesSnapshot(): Map<String, CurrencyRateModel> =
    todayRatesStateFlow.value

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

  override suspend fun getOrFetchRate(targetCode: String, date: Instant): BigDecimal {
    // TODO handle CurrencyRateNotFoundException separately from other exceptions
    try {
      // 1. Преобразуем Instant транзакции в календарную дату ('YYYY-MM-DD')
      val dateString = instantToDateString(date, TimeZone.currentSystemDefault())

      // Попытка получить курс из локального кэша
      val cachedRate = ratesCache[dateString]?.get(targetCode)

      if (cachedRate != null) {
        return cachedRate.rateMultiplier
      }

      // Попытка получить курс из базы данных
      val rateFromDb = currencyRatesDao.retrieveCurrencyRate(targetCode, dateString)

      if (rateFromDb != null) {
        val ratesCacheByDate = ratesCache[dateString]

        val updatedCache: MutableMap<String, CurrencyRateEntity> = if (ratesCacheByDate == null) {
          mutableMapOf(rateFromDb.targetCurrencyCode to rateFromDb)
        } else {
          ratesCacheByDate[targetCode] = rateFromDb
          ratesCacheByDate
        }

        ratesCache[dateString] = updatedCache

        return rateFromDb.rateMultiplier
      }

      // Если курса нет в кэше и базе, запрашиваем его из API
      val localDateTime = date.toLocalDateTime(TimeZone.currentSystemDefault())
      val ratesFromServer =
        fetchCurrencyRateEntities(Year.of(localDateTime.year), localDateTime.month, localDateTime.dayOfMonth)

      currencyRatesDao.save(ratesFromServer)

      // TODO move to CurrencyCacheManager
      ratesCache[dateString] = ratesFromServer.associateBy { it.targetCurrencyCode }.toMutableMap()

      return ratesFromServer
        .firstOrNull { it.targetCurrencyCode == targetCode }
        ?.rateMultiplier
        ?: throw CurrencyRateNotFoundException()

    } catch (e: Exception) {
      Timber.e(e, "Error fetching currency rate for $targetCode on $date")
      throw CurrencyRateNotFoundException()
    }
  }

  override suspend fun getOrFetchRates(date: Instant): Map<String, CurrencyRateModel> {
    // TODO handle CurrencyRateNotFoundException separately from other exceptions
    try {
      // 1. Преобразуем Instant транзакции в календарную дату ('YYYY-MM-DD')
      val dateString = instantToDateString(date, TimeZone.currentSystemDefault())

      // Попытка получить курс из локального кэша
      val cachedRates = ratesCache[dateString]

      if (cachedRates != null) {
        return cachedRates.mapValues { toCurrencyRateModel(it.value) }
      }

      // Попытка получить курс из базы данных
      val ratesFromDb = currencyRatesDao.retrieveCurrencyRates(dateString)

      if (ratesFromDb.isNotEmpty()) {
        val ratesMap = ratesFromDb.associateBy { it.targetCurrencyCode }.toMutableMap()
        ratesCache[dateString] = ratesMap
        return ratesMap.mapValues { toCurrencyRateModel(it.value) }
      }

      // Если курса нет в кэше и базе, запрашиваем его из API
      val localDateTime = date.toLocalDateTime(TimeZone.currentSystemDefault())
      val ratesFromServer =
        fetchCurrencyRateEntities(Year.of(localDateTime.year), localDateTime.month, localDateTime.dayOfMonth)

      currencyRatesDao.save(ratesFromServer)

      // TODO move to CurrencyCacheManager
      ratesCache[dateString] = ratesFromServer.associateBy { it.targetCurrencyCode }.toMutableMap()

      return ratesFromServer.associate { it.targetCurrencyCode to toCurrencyRateModel(it) }
    } catch (e: Exception) {
      throw CurrencyRateNotFoundException()
    }
  }

  private suspend fun fetchCurrencyRateEntities(): List<CurrencyRateEntity> =
    network.getCurrencyRates().rates.map(::toCurrencyRateEntity)

  private suspend fun fetchCurrencyRateEntities(year: Year, month: Month, day: Int): List<CurrencyRateEntity> =
    network.getCurrencyRatesDate(year, month, day.toString()).rates.map(::toCurrencyRateEntity)
}

