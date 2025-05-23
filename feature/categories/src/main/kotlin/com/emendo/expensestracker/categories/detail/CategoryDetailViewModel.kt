package com.emendo.expensestracker.categories.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.base.api.screens.SelectColorScreenApi
import com.emendo.expensestracker.app.base.api.screens.SelectIconScreenApi
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.categories.common.CategoryViewModel
import com.emendo.expensestracker.categories.common.SubcategoryUiModel
import com.emendo.expensestracker.categories.destinations.CategoryDetailRouteDestination
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.model.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CATEGORY_DETAIL_DELETE_CATEGORY_DIALOG = "category_detail_delete_category_dialog"
private const val KEY_CATEGORY_MODEL = "CategoryModel"

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val categoryRepository: CategoryRepository,
  override val selectIconScreenApi: SelectIconScreenApi,
  override val selectColorScreenApi: SelectColorScreenApi,
) : CategoryViewModel<CategoryDetailScreenDataImpl>(),
    ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate(),
    CategoryDetailsCommandReceiver {

  private val categoryId: Long by lazy { savedStateHandle[CategoryDetailRouteDestination.arguments[0].name]!! }
  private var updateCategoryJob: Job? = null
  private var categoryType: CategoryType?
    get() = savedStateHandle.get<Int>(KEY_CATEGORY_MODEL)?.let { CategoryType.getById(it) }
    set(value) {
      savedStateHandle[KEY_CATEGORY_MODEL] = value?.id
    }

  init {
    if (state.value.dataValue() == null) {
      viewModelScope.launch {
        val category: CategoryModel = categoryRepository.getCategorySnapshotById(categoryId)!! // Todo handle error case

        categoryType = category.type
        // Todo handle error case
        _state.update { UiState.Data(getDefaultCategoryDetailScreenData(category)) }
      }
    }
  }

  override fun updateCategory() {
    if (updateCategoryJob != null) {
      return
    }

    updateCategoryJob =
      viewModelScope.launch {
        with(state.value.requireDataValue().categoryScreenData) {
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

  override fun showDeleteCategoryBottomSheet() {
    showModalBottomSheet(
      GeneralBottomSheetData
        .Builder(
          id = CATEGORY_DETAIL_DELETE_CATEGORY_DIALOG,
          positiveAction = Action(resourceValueOf(R.string.delete), ::deleteCategory),
        )
        .title(resourceValueOf(R.string.dialog_category_detail_delete_confirm_title))
        .negativeAction(Action(resourceValueOf(R.string.cancel), ::hideModalBottomSheet))
        .build(),
    )
  }

  private fun deleteCategory() {
    viewModelScope.launch {
      categoryRepository.deleteCategory(categoryId)
      navigateUp()
    }
  }
}

private fun getDefaultCategoryDetailScreenData(categoryModel: CategoryModel) =
  with(categoryModel) {
    CategoryDetailScreenDataImpl(
      CategoryScreenData(
        title = name,
        icon = icon,
        color = color,
        confirmButtonEnabled = false,
        subcategories = subcategories
          .map {
            SubcategoryUiModel(
              id = it.id,
              name = it.name.textValueOrBlank(),
              icon = it.icon,
            )
          }
          .toPersistentList(),
      ),
    )
  }
