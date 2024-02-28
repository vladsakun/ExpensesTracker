package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.requireDataValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface CategoryStateManager<T> {

  val _state: MutableStateFlow<UiState<CategoryScreenData<T>>>
  val state: StateFlow<UiState<CategoryScreenData<T>>>

  val selectedColorId: Int
    get() = state.value.requireDataValue().color.id
  val selectedIconId: Int
    get() = state.value.requireDataValue().icon.id

  fun updateTitle(title: String)
  fun updateConfirmButtonEnabled(enabled: Boolean)
  fun updateColor(colorId: Int)
  fun updateIcon(iconId: Int)

  fun changeTitle(newTitle: String) {
    updateTitle(newTitle)
    updateConfirmButtonEnabled(newTitle.isNotBlank())
  }
}