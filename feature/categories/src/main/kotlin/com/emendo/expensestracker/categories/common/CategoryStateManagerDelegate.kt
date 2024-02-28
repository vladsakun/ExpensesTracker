package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.textValueOf
import com.emendo.expensestracker.model.ui.updateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryStateManagerDelegate<T>(defaultState: UiState<CategoryScreenData<T>>? = null) : CategoryStateManager<T> {

  override val _state = MutableStateFlow(defaultState ?: UiState.Loading())
  override val state = _state.asStateFlow()

  override fun updateTitle(title: String) {
    _state.updateData { it.copy(title = textValueOf(title)) }
  }

  override fun updateConfirmButtonEnabled(enabled: Boolean) {
    _state.updateData { it.copy(confirmButtonEnabled = enabled) }
  }

  override fun updateColor(colorId: Int) {
    _state.updateData { it.copy(color = ColorModel.getById(colorId)) }
  }

  override fun updateIcon(iconId: Int) {
    _state.updateData { it.copy(icon = IconModel.getById(iconId)) }
  }
}