package com.emendo.expensestracker.categories.common

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.app.base.api.screens.SelectIconScreenApi
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
  abstract val selectIconScreenApi: SelectIconScreenApi
  abstract val selectColorScreenApi: SelectIconScreenApi

  fun getSelectIconScreenRoute(): String =
    selectIconScreenApi.getSelectIconScreenRoute(selectedIconId)

  //  override fun openSelectIconScreen() {
  //    appNavigationEventBus.navigate(AppNavigationEvent.SelectIcon(selectedIconId))
  //  }

  fun getSelectColorScreenRoute(): String =
    selectColorScreenApi.getSelectIconScreenRoute(selectedColorId)
}