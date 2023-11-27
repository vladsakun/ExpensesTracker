package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.mapper.CurrencyMapper
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.datastore.ExpePreferencesDataStore
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(
  @ApplicationScope scope: CoroutineScope,
  private val expePreferencesDataStore: ExpePreferencesDataStore,
  private val currencyMapper: CurrencyMapper,
) : UserDataRepository {

  override val generalCurrency: Flow<CurrencyModel> =
    expePreferencesDataStore.generalCurrencyCode.map {
      currencyMapper.map(it)
    }

  override val userData: Flow<UserData> =
    expePreferencesDataStore.userData

  private var lastUserData: UserData? = null

  init {
    userData
      .onEach { lastUserData = it }
      .stateIn(scope, SharingStarted.WhileSubscribed(), null)
  }

  override val favouriteCurrencies: Flow<Set<CurrencyModel>> =
    expePreferencesDataStore.favouriteCurrenciesCodes.map { currencyCodes ->
      currencyCodes.map { currencyMapper.map(it) }.toSet()
    }

  override suspend fun shouldUseDynamicColor() =
    expePreferencesDataStore.shouldUseDynamicColor()

  override suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
    expePreferencesDataStore.setUseDynamicColor(useDynamicColor)
  }

  override suspend fun getGeneralCurrencyCode() =
    expePreferencesDataStore.getGeneralCurrencyCode()

  override suspend fun setGeneralCurrencyCode(currencyCode: String) {
    expePreferencesDataStore.setGeneralCurrencyCode(currencyCode)
  }

  override suspend fun addFavouriteCurrency(currencyCode: String) {
    expePreferencesDataStore.addFavouriteCurrencyCode(currencyCode)
  }

  override fun getUserDataSnapshot(): UserData? = lastUserData
}