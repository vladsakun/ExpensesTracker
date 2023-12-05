package com.emendo.expensestracker.accounts.common.navigator

import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus

interface AccountScreenNavigator {
  val appNavigationEventBus: AppNavigationEventBus
  val accountStateManager: AccountStateManager

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