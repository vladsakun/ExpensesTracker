package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.app.base.api.AppNavigationEvent
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus

interface CategoryScreenNavigator {
  val appNavigationEventBus: AppNavigationEventBus
  val stateManager: CategoryStateManager<*>

  fun openSelectIconScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectIcon(stateManager.selectedIconId))
  }

  fun openSelectColorScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectColor(stateManager.selectedColorId))
  }
}