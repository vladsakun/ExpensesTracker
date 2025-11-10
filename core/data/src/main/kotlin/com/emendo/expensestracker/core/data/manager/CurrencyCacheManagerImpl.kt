package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.app.common.ext.stateInEagerly
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.core.model.data.currency.toCurrencyModel
import com.emendo.expensestracker.data.api.manager.CurrencyCacheManager
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.repository.CurrencyRateRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import java.util.Currency
import javax.inject.Inject

class CurrencyCacheManagerImpl @Inject constructor(
  currencyRatesRepository: CurrencyRateRepository,
  @ApplicationScope private val scope: CoroutineScope,
  private val userDataRepository: UserDataRepository,
  private val expeLocaleManager: ExpeLocaleManager,
) : CurrencyCacheManager {

  override val currencyCodes: StateFlow<Map<String, CurrencyModel>?> =
    combine(expeLocaleManager.localeState, currencyRatesRepository.getCurrencyCodes()) { locale, codes ->
      codes.toCurrenciesMap()
    }.stateInEagerly(scope, null)

  override fun getCurrenciesMapSnapshot(): Map<String, CurrencyModel>? =
    currencyCodes.value

  override fun getGeneralCurrencySnapshot(): CurrencyModel {
    val generalCurrencyCode = userDataRepository.getUserDataSnapshot()?.generalCurrencyCode
    val currency = generalCurrencyCode?.let(Currency::getInstance) ?: getLocaleCurrency()
    return currency.toCurrencyModel()
  }

  private fun getLocaleCurrency(): Currency = Currency.getInstance(expeLocaleManager.getLocale())

  private fun List<String>.toCurrenciesMap(): Map<String, CurrencyModel> {
    val currencyModelsList = map { CurrencyModel.toCurrencyModel(it) }
    return currencyModelsList.associateBy { it.currencyCode }
  }
}