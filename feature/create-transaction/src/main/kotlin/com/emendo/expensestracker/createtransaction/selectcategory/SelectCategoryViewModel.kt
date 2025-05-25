package com.emendo.expensestracker.createtransaction.selectcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.categories.api.CreateCategoryScreenApi
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.domain.category.GetUserCreatedCategoriesSnapshotUseCase
import com.emendo.expensestracker.core.domain.category.GetUserCreatedCategoriesUseCase
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
  getUserCreatedCategoriesUseCase: GetUserCreatedCategoriesUseCase,
  getUserCreatedCategoriesSnapshotUseCase: GetUserCreatedCategoriesSnapshotUseCase,
  private val createTransactionController: CreateTransactionController,
  private val createCategoryScreenApi: CreateCategoryScreenApi,
) : ViewModel(), ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate() {

  private val categoryTypeFromRepository: CategoryType by lazy {
    (createTransactionController.getTargetSnapshot() as? CategoryModel)?.type
      ?: CategoryType.EXPENSE // Todo remove hardcoded
  }

  val selectCategoryUiState: StateFlow<SelectCategoryUiState> =
    categoriesUiState(getUserCreatedCategoriesUseCase, categoryTypeFromRepository)
      .stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = getDisplayCategoriesState(
          categories = getUserCreatedCategoriesSnapshotUseCase(),
          categoryType = categoryTypeFromRepository,
        ),
      )

  fun saveCategory(category: CategoryModel) {
    createTransactionController.setTarget(category)
  }

  fun getCreateCategoryScreenRoute(): String =
    createCategoryScreenApi.getRoute(categoryTypeFromRepository)
}

private fun categoriesUiState(
  getUserCreatedCategoriesUseCase: GetUserCreatedCategoriesUseCase,
  categoryType: CategoryType,
): Flow<SelectCategoryUiState> =
  getUserCreatedCategoriesUseCase().asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> getDisplayCategoriesState(categoriesResult.data, categoryType)
      is Result.Error -> SelectCategoryUiState.Error("No categories found ${categoriesResult.exception}")
      is Result.Loading -> SelectCategoryUiState.Loading
      is Result.Idle -> SelectCategoryUiState.Empty
    }
  }

private fun getDisplayCategoriesState(
  categories: List<CategoryModel>,
  categoryType: CategoryType,
): SelectCategoryUiState.DisplayCategoryList {
  val categoriesByType = categories
    .filter { it.type == categoryType }
    .toImmutableList()
  return SelectCategoryUiState.DisplayCategoryList(categoriesByType)
}