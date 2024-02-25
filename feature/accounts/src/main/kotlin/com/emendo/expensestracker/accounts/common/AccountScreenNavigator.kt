package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.app.base.api.AppNavigationEvent
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus

interface AccountScreenNavigator {
  val appNavigationEventBus: AppNavigationEventBus
  val accountStateManager: AccountStateManager<*>

  fun openSelectIconScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectIcon(accountStateManager.selectedIconId))
  }

  fun openSelectColorScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectColor(accountStateManager.selectedColorId))
  }

  fun openSelectCurrencyScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectCurrency)
  }
}