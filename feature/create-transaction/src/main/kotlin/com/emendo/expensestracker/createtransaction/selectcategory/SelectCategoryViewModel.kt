package com.emendo.expensestracker.createtransaction.selectcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.domain.category.GetUserCreatedCategoriesUseCase
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManagerDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
  categoryRepository: CategoryRepository,
  getUserCreatedCategoriesUseCase: GetUserCreatedCategoriesUseCase,
  private val createTransactionRepository: CreateTransactionRepository,
  private val appNavigationEventBus: AppNavigationEventBus,
) : ViewModel(), BottomSheetStateManager by BottomSheetStateManagerDelegate() {

  private val categoryTypeFromRepository: CategoryType by lazy {
    (createTransactionRepository.getTargetSnapshot() as? CategoryModel)?.type
      ?: CategoryType.EXPENSE // Todo remove hardcoded
  }

  val selectCategoryUiState: StateFlow<SelectCategoryUiState> =
    categoriesUiState(getUserCreatedCategoriesUseCase, categoryTypeFromRepository)
      .stateInWhileSubscribed(
        scope = viewModelScope,
        initialValue = getDisplayCategoriesState(
          categories = categoryRepository.categoriesSnapshot,
          categoryType = categoryTypeFromRepository,
        ),
      )

  fun saveCategory(category: CategoryModel) {
    createTransactionRepository.setTarget(category)
  }

  fun openCreateCategoryScreen() {
    appNavigationEventBus.navigate(AppNavigationEvent.CreateCategory(categoryTypeFromRepository))
  }
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
      is Result.Empty -> SelectCategoryUiState.Empty
    }
  }

private fun getDisplayCategoriesState(
  categories: List<CategoryModel>,
  categoryType: CategoryType,
): SelectCategoryUiState.DisplayCategoryList {
  val categoriesByType = categories.filter { it.type == categoryType }
  return SelectCategoryUiState.DisplayCategoryList(categoriesByType.toImmutableList())
}