package com.emendo.expensestracker.categories.common

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.app.base.api.AppNavigationEvent
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.categories.common.command.CategoryCommandReceiver
import com.emendo.expensestracker.categories.common.state.CategoryStateManager
import com.emendo.expensestracker.categories.common.state.CategoryStateManagerDelegate
import com.emendo.expensestracker.model.ui.UiState

abstract class CategoryViewModel<T : CategoryScreenState>(
  private val defaultState: UiState.Data<T>? = null,
) : ViewModel(),
    CategoryStateManager<T> by CategoryStateManagerDelegate(defaultState),
    CategoryCommandReceiver {

  abstract val appNavigationEventBus: AppNavigationEventBus

  override fun openSelectIconScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectIcon(selectedIconId))
  }

  override fun openSelectColorScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.SelectColor(selectedColorId))
  }
}