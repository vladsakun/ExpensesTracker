package com.emendo.expensestracker.categories.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.categories.common.CategoryDelegate
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.textValueOf
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val categoryRepository: CategoryRepository,
) : ViewModel(), CategoryDelegate {

  private val _state = MutableStateFlow(CreateCategoryScreenData.getDefault())
  override val state = _state.asStateFlow()

  private var createCategoryJob: Job? = null
  private val categoryType: CategoryType = savedStateHandle[CreateCategoryRouteDestination.arguments[0].name]!!

  override fun updateTitle(title: String) {
    _state.update { it.copy(title = textValueOf(title)) }
  }

  fun updateColor(colorId: Int) {
    _state.update { it.copy(color = ColorModel.getById(colorId)) }
  }

  fun updateIcon(iconId: Int) {
    _state.update { it.copy(icon = IconModel.getById(iconId)) }
  }

  override fun updateConfirmButtonEnabled(enabled: Boolean) {
    _state.update { it.copy(confirmButtonEnabled = enabled) }
  }

  fun consumeNavigateUpEvent() {
    _state.update { it.copy(navigateUpEvent = consumed) }
  }

  fun createCategory() {
    if (createCategoryJob != null) {
      return
    }

    createCategoryJob = viewModelScope.launch {
      with(state.value) {
        categoryRepository.createCategory(
          name = title.value,
          icon = icon,
          color = color,
          type = categoryType,
        )
      }
      _state.update { it.copy(navigateUpEvent = triggered) }
    }
  }
}