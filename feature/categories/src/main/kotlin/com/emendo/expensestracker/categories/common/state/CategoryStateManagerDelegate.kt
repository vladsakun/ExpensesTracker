package com.emendo.expensestracker.categories.common.state

import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.categories.common.CategoryScreenState
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.dataValue
import com.emendo.expensestracker.model.ui.textValueOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategoryStateManagerDelegate<T>(defaultState: UiState<T>? = null) :
  CategoryStateManager<T> where T : CategoryScreenState {

  override val _state: MutableStateFlow<UiState<T>> = MutableStateFlow(defaultState ?: UiState.Loading())
  override val state: StateFlow<UiState<T>> = _state.asStateFlow()

  override fun updateTitle(title: String) {
    _state.updateScreenDataData {
      it.copy(title = textValueOf(title))
    }
  }

  override fun updateConfirmButtonEnabled(enabled: Boolean) {
    _state.updateScreenDataData {
      it.copy(confirmButtonEnabled = enabled)
    }
  }

  override fun updateColor(colorId: Int) {
    _state.updateScreenDataData {
      it.copy(color = ColorModel.getById(colorId))
    }
  }

  override fun updateIcon(iconId: Int) {
    _state.updateScreenDataData {
      it.copy(icon = IconModel.getById(iconId))
    }
  }
}

fun <T> MutableStateFlow<UiState<T>>.updateScreenDataData(updateFunction: (CategoryScreenData) -> CategoryScreenData) where T : CategoryScreenState {
  update {
    val data = it.dataValue()?.categoryScreenData ?: return@update it
    val updatedScreenData = updateFunction(data)
    val newData: CategoryScreenState? = it.dataValue()?.copyScreenData(updatedScreenData)
    UiState.Data(data = newData as T)
  }
}