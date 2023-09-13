package com.emendo.categories.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CategoriesListViewModel @Inject constructor(
  categoryRepository: CategoryRepository,
) : ViewModel() {

  val uiState: StateFlow<CategoriesListUiState> = categoriesUiState(categoryRepository)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = CategoriesListUiState.Loading
    )
}

private fun categoriesUiState(categoryRepository: CategoryRepository): Flow<CategoriesListUiState> {
  return categoryRepository.getCategories().asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> CategoriesListUiState.DisplayCategoriesList(categoriesResult.data.toImmutableList())
      is Result.Error -> CategoriesListUiState.Error("Error")
      is Result.Loading -> CategoriesListUiState.Loading
    }
  }
}