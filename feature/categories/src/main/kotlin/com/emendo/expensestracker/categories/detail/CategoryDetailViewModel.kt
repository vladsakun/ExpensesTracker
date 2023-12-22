package com.emendo.expensestracker.categories.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.categories.common.CategoryDelegate
import com.emendo.expensestracker.categories.destinations.CategoryDetailScreenDestination
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.*
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.domain.category.GetCategorySnapshotByIdUseCase
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val categoryRepository: CategoryRepository,
  private val getCategorySnapshotByIdUseCase: GetCategorySnapshotByIdUseCase,
) : ViewModel(), ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate(), CategoryDelegate {

  private val categoryId: Long by lazy { savedStateHandle[CategoryDetailScreenDestination.arguments[0].name]!! }
  private val category by lazy { getCategorySnapshotByIdUseCase(categoryId) }

  private val _state = MutableStateFlow(CategoryDetailScreenData.getDefault(category))
  override val state = _state.asStateFlow()

  private var updateCategoryJob: Job? = null

  override fun updateTitle(title: String) {
    _state.update { it.copy(title = textValueOf(title)) }
  }

  override fun updateConfirmButtonEnabled(enabled: Boolean) {
    _state.update { it.copy(confirmButtonEnabled = enabled) }
  }

  fun updateColor(colorId: Int) {
    _state.update { it.copy(color = ColorModel.getById(colorId)) }
  }

  fun updateIcon(iconId: Int) {
    _state.update { it.copy(icon = IconModel.getById(iconId)) }
  }

  fun updateCategory() {
    if (updateCategoryJob != null) {
      return
    }

    updateCategoryJob = viewModelScope.launch {
      with(state.value) {
        categoryRepository.updateCategory(
          id = categoryId,
          name = title.textValueOrBlank(),
          icon = icon,
          color = color,
          type = category.type,
        )
      }

      navigateUp()
    }
  }

  fun showDeleteCategoryBottomSheet() {
    showModalBottomSheet(
      GeneralBottomSheetData.Builder(Action(resourceValueOf(R.string.delete), ::deleteCategory))
        .title(resourceValueOf(R.string.category_detail_dialog_delete_confirm_title))
        .negativeAction(Action(resourceValueOf(R.string.cancel), ::hideModalBottomSheet))
        .build()
    )
  }

  private fun deleteCategory() {
    viewModelScope.launch {
      categoryRepository.deleteCategory(categoryId)
      navigateUp()
    }
  }
}