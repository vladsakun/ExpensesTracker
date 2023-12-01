package com.emendo.expensestracker.createtransaction.selectcategory

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
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
  categoryRepository: CategoryRepository,
  private val createTransactionRepository: CreateTransactionRepository,
  private val appNavigationEventBus: AppNavigationEventBus,
) : BaseBottomSheetViewModel<BottomSheetType>() {

  private val categoryTypeFromRepository: CategoryType by lazy {
    (createTransactionRepository.getTargetSnapshot() as? CategoryModel)?.type
      ?: CategoryType.EXPENSE // Todo remove hardcoded
  }

  val selectCategoryUiState: StateFlow<SelectCategoryUiState> =
    categoriesUiState(
      categoryRepository,
      categoryTypeFromRepository
    ).stateInWhileSubscribed(
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
  categoryRepository: CategoryRepository,
  categoryType: CategoryType,
): Flow<SelectCategoryUiState> {
  return categoryRepository.categories.asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> getDisplayCategoriesState(categoriesResult.data, categoryType)
      is Result.Error -> SelectCategoryUiState.Error("No categories found ${categoriesResult.exception}")
      is Result.Loading -> SelectCategoryUiState.Loading
      is Result.Empty -> SelectCategoryUiState.Empty
    }
  }
}

private fun getDisplayCategoriesState(
  categories: List<CategoryModel>,
  categoryType: CategoryType,
): SelectCategoryUiState.DisplayCategoryList {
  val categoriesByType = categories.filter { it.type == categoryType }
  return SelectCategoryUiState.DisplayCategoryList(categoriesByType.toImmutableList())
}