package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.datastore.ExpePreferencesDataStore
import com.emendo.expensestracker.core.model.data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(
  @ApplicationScope scope: CoroutineScope,
  private val expePreferencesDataStore: ExpePreferencesDataStore,
) : UserDataRepository {

  override val generalCurrencyCode: Flow<String> =
    expePreferencesDataStore.generalCurrencyCode

  override val userData: Flow<UserData> =
    expePreferencesDataStore.userData

  // Todo remove workaround
  private var lastUserData: UserData? = null

  // Todo remove workaround
  init {
    userData
      .onEach { lastUserData = it }
      .launchIn(scope)
  }

  override val favouriteCurrencies: Flow<Set<String>> =
    expePreferencesDataStore.favouriteCurrenciesCodes

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

  override fun getLastUserData(): UserData? =
    lastUserData
}