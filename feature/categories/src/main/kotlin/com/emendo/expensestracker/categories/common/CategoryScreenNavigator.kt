package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.app.base.api.AppNavigationEvent
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus

interface CategoryScreenNavigator {
  val appNavigationEventBus: AppNavigationEventBus
  val categoryDelegate: CategoryDelegate

  fun openSelectIconScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectIcon(categoryDelegate.selectedIconId))
  }

  fun openSelectColorScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectColor(categoryDelegate.selectedColorId))
  }
}