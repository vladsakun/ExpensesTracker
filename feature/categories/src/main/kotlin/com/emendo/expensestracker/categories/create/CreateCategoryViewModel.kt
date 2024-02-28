package com.emendo.expensestracker.categories.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.categories.common.CategoryScreenNavigator
import com.emendo.expensestracker.categories.common.CategoryStateManager
import com.emendo.expensestracker.categories.common.CategoryStateManagerDelegate
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.model.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val categoryRepository: CategoryRepository,
  override val appNavigationEventBus: AppNavigationEventBus,
) : ViewModel(),
    CategoryStateManager<CreateCategoryAdditionalScreenData> by CategoryStateManagerDelegate(UiState.Data(getDefault())),
    CategoryScreenNavigator {

  override val stateManager: CategoryStateManager<*>
    get() = this

  private var createCategoryJob: Job? = null
  private val categoryType: CategoryType = savedStateHandle[CreateCategoryRouteDestination.arguments[0].name]!!

  fun consumeNavigateUpEvent() {
    _state.updateAdditionalData {
      it?.copy(navigateUpEvent = consumed)
    }
  }

  fun createCategory() {
    if (createCategoryJob != null) {
      return
    }

    createCategoryJob = viewModelScope.launch {
      with(state.value.requireDataValue()) {
        categoryRepository.createCategory(
          name = title.textValueOrBlank(),
          icon = icon,
          color = color,
          type = categoryType,
        )
      }
      _state.updateAdditionalData {
        it?.copy(navigateUpEvent = triggered)
      }
    }
  }
}

private fun MutableStateFlow<UiState<CreateCategoryScreenData>>.updateAdditionalData(
  function: (CreateCategoryAdditionalScreenData?) -> CreateCategoryAdditionalScreenData?,
) {
  updateData {
    it.copy(additionalData = function(it.additionalData))
  }
}

fun <T> MutableStateFlow<UiState<T>>.updateData(
  function: (T) -> T,
) {
  update { state ->
    if (state is UiState.Data) {
      state.copy(data = function(state.data))
    } else {
      state
    }
  }
}

fun getDefault() = CreateCategoryScreenData(
  title = textValueOf(""),
  icon = IconModel.random,
  color = ColorModel.random,
  confirmButtonEnabled = false,
  additionalData = CreateCategoryAdditionalScreenData(),
)