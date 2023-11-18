package com.emendo.expensestracker.categories.create

import androidx.lifecycle.SavedStateHandle
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BaseBottomSheetViewModel<BottomSheetType>() {

  private val _state = MutableStateFlow(CreateCategoryScreenData.getDefault())
  val state = _state.asStateFlow()

  private var createCategoryJob: Job? = null

  private val categoryType = savedStateHandle[CreateCategoryRouteDestination.arguments[0].name] ?: CategoryType.EXPENSE

  fun changeTitle(newTitle: String) {
    _state.update {
      it.copy(
        title = newTitle,
        isCreateButtonEnabled = newTitle.isNotBlank(),
      )
    }
  }

  fun showIconBottomSheet() {
    showBottomSheet(BottomSheetType.Icon(state.value.icon, ::selectIcon))
  }

  fun showColorBottomSheet() {
    showBottomSheet(BottomSheetType.Color(state.value.color, ::selectColor))
  }

  fun createCategory() {
    if (createCategoryJob != null) {
      return
    }

    createCategoryJob = vmScope.launch(ioDispatcher) {
      categoryRepository.upsertCategory(
        CategoryModel(
          name = state.value.title,
          icon = state.value.icon,
          color = state.value.color,
          type = categoryType,
        )
      )
      navigateUp()
    }
  }

  private fun selectIcon(iconModel: IconModel) {
    _state.update { it.copy(icon = iconModel) }
  }

  private fun selectColor(colorModel: ColorModel) {
    _state.update { it.copy(color = colorModel) }
  }
}