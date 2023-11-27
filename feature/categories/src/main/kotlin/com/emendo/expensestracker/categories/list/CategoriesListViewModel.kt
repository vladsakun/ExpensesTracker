package com.emendo.expensestracker.categories.list

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryType.Companion.label
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.domain.GetCategoriesWithTotalTransactionsUseCase
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CategoriesListViewModel @Inject constructor(
  getCategoriesWithTotalTransactionsUseCase: GetCategoriesWithTotalTransactionsUseCase,
  private val appNavigationEventBus: AppNavigationEventBus,
  private val createTransactionRepository: CreateTransactionRepository,
) : BaseBottomSheetViewModel<BottomSheetType>() {

  val categoriesListUiState: StateFlow<CategoriesListUiState> =
    categoriesUiState(getCategoriesWithTotalTransactionsUseCase)
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CategoriesListUiState.Empty,
      )

  private var selectedPageIndex = DEFAULT_PAGE_INDEX

  fun showCalculatorBottomSheet(category: CategoryWithTotalTransactions) {
    createTransactionRepository.setTarget(category.categoryModel)
    appNavigationEventBus.navigate(AppNavigationEvent.CreateTransaction(target = category.categoryModel, source = null))
  }

  fun pageSelected(pageIndex: Int) {
    selectedPageIndex = pageIndex
  }

  fun getCategoryType(): CategoryType {
    return selectedPageIndex.toCategoryType()
  }

  companion object {
    private val DEFAULT_PAGE_INDEX = CategoryType.EXPENSE.toPageIndex()
  }
}

private fun CategoryType.toPageIndex(): Int =
  when (this) {
    CategoryType.EXPENSE -> 0
    CategoryType.INCOME -> 1
  }

private fun Int.toCategoryType(): CategoryType =
  when (this) {
    0 -> CategoryType.EXPENSE
    1 -> CategoryType.INCOME
    else -> throw IllegalArgumentException("Category type with index $this is not supported")
  }

private fun categoriesUiState(
  getCategoriesWithTotalTransactionsUseCase: GetCategoriesWithTotalTransactionsUseCase,
): Flow<CategoriesListUiState> {
  return getCategoriesWithTotalTransactionsUseCase().asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> {
        CategoriesListUiState.DisplayCategoriesList(
          categories = persistentMapOf(
            createCategoryPagePair(CategoryType.EXPENSE, categoriesResult.data),
            createCategoryPagePair(CategoryType.INCOME, categoriesResult.data),
          ),
          tabs = persistentListOf(
            TabData(CategoryType.EXPENSE.label),
            TabData(CategoryType.INCOME.label),
          )
        )
      }

      is Result.Error -> CategoriesListUiState.Error("No categories found ${categoriesResult.exception}")
      is Result.Loading -> CategoriesListUiState.Loading
      is Result.Empty -> CategoriesListUiState.Empty
    }
  }
}

private fun createCategoryPagePair(
  categoryType: CategoryType,
  categoriesList: List<CategoryWithTotalTransactions>,
): Pair<Int, ImmutableList<CategoryWithTotalTransactions>> {
  return categoryType.toPageIndex() to categoriesList.filter { it.categoryModel.type == categoryType }.toImmutableList()
}