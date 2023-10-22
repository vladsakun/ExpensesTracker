package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.data.repository.api.CurrencyRatesRepository
import com.emendo.expensestracker.core.data.repository.api.CurrencyRepository
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

class OfflineFirstCurrencyRepository @Inject constructor(
  private val userDataRepository: UserDataRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val currencyRatesRepository: CurrencyRatesRepository,
) : CurrencyRepository {

  private var cachedCurrenciesList: Map<String, CurrencyModel>? = null
  private var lastCurrencyLocale = Locale.getDefault()

  override val currenciesList: Map<String, CurrencyModel>
    get() =
      if (lastCurrencyLocale == Locale.getDefault()) {
        cachedCurrenciesList ?: populateCurrenciesList()
      } else {
        populateCurrenciesList()
      }

  override val generalCurrency: Flow<CurrencyModel> =
    userDataRepository.generalCurrencyCode.map(::findCurrencyModel)

  override val favouriteCurrencies: Flow<List<CurrencyModel>> =
    userDataRepository.favouriteCurrencies.map { currencyCode ->
      currencyCode.map(::findCurrencyModel)
    }

  override fun initCurrenciesMap(supportedCurrencyCodes: List<String>) {
    setupCurrenciesMap(supportedCurrencyCodes)
  }

  override suspend fun markAsFavourite(currencyModel: CurrencyModel) {
    withContext(ioDispatcher) {
      userDataRepository.addFavouriteCurrency(currencyModel.currencyCode)
    }
  }

  override fun getLastGeneralCurrency(): CurrencyModel? =
    userDataRepository.getLastUserData()?.generalCurrencyCode?.let(::toCurrencyModel)

  override fun findCurrencyModel(currencyCode: String): CurrencyModel =
    currenciesList[currencyCode] ?: throw IllegalArgumentException("Currency code: $currencyCode not found")

  private fun setupCurrenciesMap(supportedCurrencyCodes: List<String>): Map<String, CurrencyModel> {
    lastCurrencyLocale = Locale.getDefault()
    return createCurrencyModelList(supportedCurrencyCodes)
  }

  private fun populateCurrenciesList(): Map<String, CurrencyModel> {
    val supportedCurrencyCodes = runBlocking(Dispatchers.Default) {
      currencyRatesRepository.retrieveAllCurrencyCodes()
    }
    val currencyModelList = setupCurrenciesMap(supportedCurrencyCodes)
    cachedCurrenciesList = currencyModelList
    return currencyModelList
  }

  private fun createCurrencyModelList(supportedCurrencyCodes: List<String>): Map<String, CurrencyModel> {
    val availableCurrencies = Currency.getAvailableCurrencies().map { it.currencyCode }
    val currencies = supportedCurrencyCodes.intersect(availableCurrencies.toSet())
    val currencyModels = currencies.map { toCurrencyModel(it) }
    return currencyModels.sortedBy { it.currencyCode }.associateBy { it.currencyCode }
  }

  private fun toCurrencyModel(currencyCode: String) =
    toCurrencyModel(Currency.getInstance(currencyCode))

  private fun toCurrencyModel(currency: Currency) =
    CurrencyModel(
      currencyCode = currency.currencyCode,
      currencyName = currency.displayName,
      currencySymbol = if (currency.symbol == currency.currencyCode) null else currency.symbol,
    )
}