package com.emendo.expensestracker.data.api.repository

import com.emendo.expensestracker.core.model.data.DarkThemeConfig
import com.emendo.expensestracker.core.model.data.UserData
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

  /**
   * Stream of [UserData]
   */
  val userData: Flow<UserData>

  /**
   * Stream of general currency
   */
  val generalCurrency: Flow<CurrencyModel>

  /**
   * Stream of favourite currencies
   */
  val favouriteCurrencies: Flow<Set<CurrencyModel>>

  /**
   * Last cached [UserData] value
   */
  fun getUserDataSnapshot(): UserData?

  suspend fun shouldUseDynamicColor(): Boolean
  suspend fun setUseDynamicColor(useDynamicColor: Boolean)

  /**
   * Sets the desired dark theme config.
   */
  suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

  suspend fun getGeneralCurrencyCode(): String
  suspend fun setGeneralCurrencyCode(currencyCode: String)
  suspend fun addFavouriteCurrency(currencyCode: String)
}