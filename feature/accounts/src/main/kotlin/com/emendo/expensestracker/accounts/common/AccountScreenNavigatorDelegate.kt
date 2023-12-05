package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus

internal class AccountScreenNavigatorDelegate(
  private val appNavigationEventBus: AppNavigationEventBus,
) : AccountScreenNavigator {

  override fun openSelectIconScreen(preselectedIconId: Int) {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectIcon(preselectedIconId))
  }

  override fun openSelectColorScreen(preselectedColorId: Int) {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectColor(preselectedColorId))
  }

  override fun openSelectCurrencyScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectCurrency)
  }
}