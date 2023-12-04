package com.emendo.expensestracker.categories.common

import kotlinx.coroutines.flow.StateFlow

interface CategoryDelegate {
  val state: StateFlow<CategoryScreenData>

  fun updateTitle(title: String)
  fun updateConfirmButtonEnabled(enabled: Boolean)

  val selectedColorId: Int
    get() = state.value.color.id
  val selectedIconId: Int
    get() = state.value.icon.id

  fun changeTitle(newTitle: String) {
    updateTitle(newTitle)
    updateConfirmButtonEnabled(newTitle.isNotBlank())
  }
}