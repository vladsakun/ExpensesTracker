package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

  /**
   * Stream of [UserData]
   */
  val userData: Flow<UserData>

  /**
   * Stream of general currency code
   */
  val generalCurrencyCode: Flow<String>

  /**
   * Stream of favourite currency codes
   */
  val favouriteCurrencies: Flow<Set<String>>

  /**
   * Last cached [UserData] value
   */
  fun getLastUserData(): UserData?

  suspend fun shouldUseDynamicColor(): Boolean
  suspend fun setUseDynamicColor(useDynamicColor: Boolean)

  suspend fun getGeneralCurrencyCode(): String
  suspend fun setGeneralCurrencyCode(currencyCode: String)
  suspend fun addFavouriteCurrency(currencyCode: String)
}