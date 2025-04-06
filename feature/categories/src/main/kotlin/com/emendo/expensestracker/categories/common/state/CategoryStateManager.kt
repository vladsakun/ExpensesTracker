package com.emendo.expensestracker.categories.common.state

import com.emendo.expensestracker.categories.common.CategoryScreenState
import com.emendo.expensestracker.categories.subcategory.CreateSubcategoryResult
import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.requireDataValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface CategoryStateManager<T : CategoryScreenState> {

  val _state: MutableStateFlow<UiState<T>>
  val state: StateFlow<UiState<T>>

  val selectedColorId: Int
    get() = state.value.requireDataValue().categoryScreenData.color.id
  val selectedIconId: Int
    get() = state.value.requireDataValue().categoryScreenData.icon.id

  fun updateTitle(title: String)
  fun updateConfirmButtonEnabled(enabled: Boolean)
  fun updateColor(colorId: Int)
  fun updateIcon(iconId: Int)
  fun addSubcategory(result: CreateSubcategoryResult)

  fun changeTitle(newTitle: String) {
    updateTitle(newTitle)
    updateConfirmButtonEnabled(newTitle.isNotBlank())
  }
}