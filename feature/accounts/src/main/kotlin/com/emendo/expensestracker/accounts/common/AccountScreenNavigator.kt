package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.app.base.api.screens.SelectColorScreenApi
import com.emendo.expensestracker.app.base.api.screens.SelectCurrencyScreenApi
import com.emendo.expensestracker.app.base.api.screens.SelectIconScreenApi

interface AccountScreenNavigator {
  val accountStateManager: AccountStateManager<*>
  val selectCurrencyScreenApi: SelectCurrencyScreenApi
  val selectIconScreenApi: SelectIconScreenApi
  val selectColorScreenApi: SelectColorScreenApi

  fun getSelectIconScreenRoute(): String =
    selectIconScreenApi.getSelectIconScreenRoute(accountStateManager.selectedIconId)

  fun getSelectColorScreenRoute(): String =
    selectColorScreenApi.getSelectColorScreenRoute(accountStateManager.selectedColorId)

  fun getSelectCurrencyScreenRoute(): String =
    selectCurrencyScreenApi.getSelectCurrencyScreenRoute()
}