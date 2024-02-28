@file:Suppress("UNCHECKED_CAST") // Todo remove this suppression

package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.categories.detail.CategoryScreenDataContract
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.textValueOf
import com.emendo.expensestracker.model.ui.updateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryStateManagerDelegate<T : CategoryScreenDataContract>(defaultState: UiState<T>? = null) :
  CategoryStateManager<T> {

  override val _state: MutableStateFlow<UiState<T>> = MutableStateFlow(defaultState ?: UiState.Loading())
  override val state: StateFlow<UiState<T>> = _state.asStateFlow()

  override fun updateTitle(title: String) {
    _state.updateData {
      it.copyMy(title = textValueOf(title)) as T
    }
  }

  override fun updateConfirmButtonEnabled(enabled: Boolean) {
    _state.updateData {
      it.copyMy(confirmButtonEnabled = enabled) as T
    }
  }

  override fun updateColor(colorId: Int) {
    _state.updateData {
      it.copyMy(color = ColorModel.getById(colorId)) as T
    }
  }

  override fun updateIcon(iconId: Int) {
    _state.updateData {
      it.copyMy(icon = IconModel.getById(iconId)) as T
    }
  }
}