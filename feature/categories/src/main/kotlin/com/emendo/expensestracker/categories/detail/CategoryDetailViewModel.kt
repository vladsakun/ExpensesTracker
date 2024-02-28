package com.emendo.expensestracker.categories.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.common.CategoryScreenNavigator
import com.emendo.expensestracker.categories.common.CategoryStateManager
import com.emendo.expensestracker.categories.common.CategoryStateManagerDelegate
import com.emendo.expensestracker.categories.destinations.CategoryDetailScreenDestination
import com.emendo.expensestracker.core.domain.category.GetCategorySnapshotByIdUseCase
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.model.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CATEGORY_DETAIL_DELETE_CATEGORY_DIALOG = "category_detail_delete_category_dialog"
private const val KEY_CATEGORY_MODEL = "CategoryModel"

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val categoryRepository: CategoryRepository,
  private val getCategorySnapshotByIdUseCase: GetCategorySnapshotByIdUseCase,
  override val appNavigationEventBus: AppNavigationEventBus,
) : ViewModel(),
    CategoryStateManager<CategoryDetailScreenDataImpl> by CategoryStateManagerDelegate(),
    ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate(),
    CategoryScreenNavigator {

  override val stateManager: CategoryStateManager<*>
    get() = this

  private val categoryId: Long by lazy { savedStateHandle[CategoryDetailScreenDestination.arguments[0].name]!! }
  private var updateCategoryJob: Job? = null
  private var categoryType: CategoryType?
    get() = savedStateHandle.get<Int>(KEY_CATEGORY_MODEL)?.let { CategoryType.getById(it) }
    set(value) {
      savedStateHandle[KEY_CATEGORY_MODEL] = value?.id
    }

  init {
    if (state.value.dataValue() == null) {
      viewModelScope.launch {
        val category: CategoryModel = getCategorySnapshotByIdUseCase(categoryId).first()

        categoryType = category.type
        // Todo handle error case
        _state.update { UiState.Data(getDefaultCategoryDetailScreenData(category)) }
      }
    }
  }

  fun updateCategory() {
    if (updateCategoryJob != null) {
      return
    }

    updateCategoryJob = viewModelScope.launch {
      with(state.value.requireDataValue()) {
        categoryRepository.updateCategory(
          id = categoryId,
          name = title.textValueOrBlank(),
          icon = icon,
          color = color,
          type = checkNotNull(categoryType),
        )
      }

      navigateUp()
    }
  }

  fun showDeleteCategoryBottomSheet() {
    showModalBottomSheet(
      GeneralBottomSheetData
        .Builder(
          id = CATEGORY_DETAIL_DELETE_CATEGORY_DIALOG,
          positiveAction = Action(resourceValueOf(R.string.delete), ::deleteCategory),
        )
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

private fun getDefaultCategoryDetailScreenData(categoryModel: CategoryModel) = with(categoryModel) {
  CategoryDetailScreenDataImpl(
    title = name,
    icon = icon,
    color = color,
    confirmButtonEnabled = false,
  )
}