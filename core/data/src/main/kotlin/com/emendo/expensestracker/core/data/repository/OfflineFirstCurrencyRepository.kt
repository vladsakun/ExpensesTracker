package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.repository.api.CurrencyRepository
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.CurrencyModels
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

class OfflineFirstCurrencyRepository @Inject constructor(
  private val userDataRepository: UserDataRepository,
) : CurrencyRepository {

  private var cachedCurrenciesList: Map<String, CurrencyModel>? = null
  private var lastCurrencyLocale = Locale.getDefault()

  override val currenciesList: Map<String, CurrencyModel>
    get() {
      return if (lastCurrencyLocale == Locale.getDefault()) {
        cachedCurrenciesList ?: populateCurrenciesList()
      } else {
        populateCurrenciesList()
      }
    }

  override val generalCurrency: Flow<CurrencyModel> =
    userDataRepository.generalCurrencyCode.map(::findCurrencyModel)

  override val favouriteCurrencies: Flow<List<CurrencyModel>> =
    userDataRepository.favouriteCurrencies.map { it.map(::findCurrencyModel) }

  override suspend fun markAsFavourite(currencyModel: CurrencyModel) {
    userDataRepository.addFavouriteCurrency(currencyModel.currencyCode)
  }

  override fun getLastGeneralCurrency(): CurrencyModel? =
    userDataRepository.getLastUserData()?.generalCurrencyCode?.let(::findCurrencyModel)

  override fun findCurrencyModel(currencyCode: String): CurrencyModel =
    currenciesList[currencyCode] ?: throw IllegalArgumentException("Currency code: $currencyCode not found")

  private fun populateCurrenciesList(): Map<String, CurrencyModel> {
    lastCurrencyLocale = Locale.getDefault()
    val currencies = CurrencyModels.currencies
    currencies.addAll(Currency.getAvailableCurrencies().map(::toCurrencyModel))
    cachedCurrenciesList = currencies.sortedBy { it.currencyCode }.associateBy { it.currencyCode }
    return cachedCurrenciesList!!
  }

  private fun toCurrencyModel(currency: Currency) = CurrencyModel(
    currencyCode = currency.currencyCode,
    currencyName = currency.displayName,
    currencySymbol = if (currency.symbol == currency.currencyCode) null else currency.symbol,
  )
}