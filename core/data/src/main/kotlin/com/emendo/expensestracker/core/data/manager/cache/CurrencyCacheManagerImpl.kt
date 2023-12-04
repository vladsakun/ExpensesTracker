package com.emendo.expensestracker.core.data.manager.cache

import com.emendo.expensestracker.core.app.common.ext.stateInEagerly
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.manager.ExpeLocaleManager
import com.emendo.expensestracker.core.data.mapper.CurrencyMapper
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import java.util.Currency
import javax.inject.Inject

class CurrencyCacheManagerImpl @Inject constructor(
  currencyRatesRepository: CurrencyRateRepository,
  @ApplicationScope private val scope: CoroutineScope,
  private val userDataRepository: UserDataRepository,
  private val currencyMapper: CurrencyMapper,
  private val expeLocaleManager: ExpeLocaleManager,
) : CurrencyCacheManager {

  override val currencyCodes: StateFlow<Map<String, CurrencyModel>?> =
    combine(expeLocaleManager.localeState, currencyRatesRepository.currencyCodes) { locale, codes ->
      codes.toCurrenciesMap()
    }.stateInEagerly(scope, null)

  override fun startCaching() {

  }

  override fun getCurrenciesMapSnapshot(): Map<String, CurrencyModel>? {
    return currencyCodes.value
  }

  override fun getGeneralCurrencySnapshot(): CurrencyModel {
    val generalCurrencyCode = userDataRepository.getUserDataSnapshot()?.generalCurrencyCode
    val currency = generalCurrencyCode?.let(Currency::getInstance) ?: getLocaleCurrency()
    return currencyMapper.toCurrencyModelBlocking(currency)
  }

  private fun getLocaleCurrency(): Currency = Currency.getInstance(expeLocaleManager.getLocale())

  private suspend fun List<String>.toCurrenciesMap(): Map<String, CurrencyModel> {
    val currencyModelsList = map { currencyMapper.map(it) }
    return currencyModelsList.associateBy { it.currencyCode }
  }
}