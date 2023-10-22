package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.data.Synchronizer
import com.emendo.expensestracker.core.data.repository.api.CurrencyRatesRepository
import com.emendo.expensestracker.core.data.suspendRunCatching
import com.emendo.expensestracker.core.database.dao.CurrencyRateDao
import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

data class CurrencyRateModel(
  val currencyCode: String,
  val rate: BigDecimal,
)

class OfflineFirstCurrencyRatesRepository @Inject constructor(
  private val network: CurrencyRatesNetworkDataSource,
  private val currencyRatesDao: CurrencyRateDao,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CurrencyRatesRepository {

  override fun getRates(): Flow<List<CurrencyRateModel>> =
    currencyRatesDao.getAll().map {
      it.map(::toCurrencyRateModel)
    }

  override suspend fun retrieveAllCurrencyCodes(): List<String> =
    currencyRatesDao.retrieveAllCurrencyCodes()

  override suspend fun populateCurrencyRatesIfEmpty(): List<String> {
    val currencyCodes = currencyRatesDao.retrieveAllCurrencyCodes()
    if (currencyCodes.isNotEmpty()) {
      return currencyCodes
    }

    withContext(ioDispatcher) {
      val currencyRateEntities = network.getCurrencyRates().rates.map(::toCurrencyRateEntity)
      currencyRatesDao.save(currencyRateEntities)
    }

    return currencyCodes
  }

  override suspend fun getRate(currencyCode: String): BigDecimal =
    currencyRatesDao.getRate(currencyCode)

  override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
    suspendRunCatching {
      with(synchronizer) {
        val currentVersion = getChangeListVersions().currencyRatesLastUpdateInstant
        if (currentVersion >= Clock.System.now().minus(24, DateTimeUnit.HOUR)) {
          return@suspendRunCatching true
        }

        val currencyRateEntities = network.getCurrencyRates().rates.map(::toCurrencyRateEntity)
        currencyRatesDao.save(currencyRateEntities)

        val latestVersion = Clock.System.now()
        updateChangeListVersions {
          copy(currencyRatesLastUpdateInstant = latestVersion)
        }
      }
    }.isSuccess
}

private fun toCurrencyRateModel(currencyRateEntity: CurrencyRateEntity) =
  CurrencyRateModel(
    currencyCode = currencyRateEntity.currencyCode,
    rate = currencyRateEntity.rate,
  )

private fun toCurrencyRateEntity(currencyRate: Map.Entry<String, Double>) = CurrencyRateEntity(
  currencyCode = currencyRate.key,
  rate = BigDecimal(currencyRate.value).setScale(4, RoundingMode.HALF_EVEN),
)

