package com.emendo.expensestracker.core.data.manager.cache

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers.IO
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManager
import com.emendo.expensestracker.core.data.mapper.CurrencyMapper
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.Currency
import javax.inject.Inject

class CurrencyCacheManagerImpl @Inject constructor(
  @ApplicationScope private val scope: CoroutineScope,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
  private val userDataRepository: UserDataRepository,
  private val currencyRatesRepository: CurrencyRateRepository,
  private val currencyMapper: CurrencyMapper,
  private val expeLocaleManager: ExpeLocaleManager,
) : CurrencyCacheManager {

  private var cachedCurrenciesMap: Map<String, CurrencyModel>? = null
  private var lastUsedLocale = expeLocaleManager.getLocale()

  override fun startCaching() {
    currencyRatesRepository
      .currencyCodes
      .onEach { rates ->
        cachedCurrenciesMap = rates.toCurrenciesMap()
      }
      .launchIn(scope)
  }

  override fun getCurrenciesBlocking(): Map<String, CurrencyModel> {
    if (lastUsedLocale != expeLocaleManager.getLocale()) {
      cachedCurrenciesMap = cachedCurrenciesMap?.mapValues { (_, currencyModel) ->
        currencyMapper.toCurrencyModelBlocking(currencyModel.currencyCode)
      }
    }

    lastUsedLocale = expeLocaleManager.getLocale()

    return cachedCurrenciesMap ?: retrieveCurrenciesMapBlocking()
  }

  override fun getGeneralCurrencySnapshot(): CurrencyModel {
    val generalCurrencyCode = userDataRepository.getUserDataSnapshot()?.generalCurrencyCode
    val currency = generalCurrencyCode?.let(Currency::getInstance) ?: getLocaleCurrency()
    return currencyMapper.toCurrencyModelBlocking(currency)
  }

  private fun retrieveCurrenciesMapBlocking() = runBlocking(ioDispatcher) {
    Timber.w("getCurrenciesBlocking: retrieving currencies from db")
    val currenciesMap = currencyRatesRepository.retrieveAllCurrencyCodes().toCurrenciesMap()
    cachedCurrenciesMap = currenciesMap
    currenciesMap
  }

  private fun getLocaleCurrency(): Currency = Currency.getInstance(expeLocaleManager.getLocale())

  private suspend fun List<String>.toCurrenciesMap(): Map<String, CurrencyModel> {
    val currencyModelsList = map { currencyMapper.map(it) }
    return currencyModelsList.associateBy { it.currencyCode }
  }
}