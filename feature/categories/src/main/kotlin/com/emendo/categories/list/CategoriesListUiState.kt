package com.emendo.categories.list

import com.emendo.expensestracker.core.data.model.Category
import kotlinx.collections.immutable.ImmutableList

sealed interface CategoriesListUiState {
  data object Loading : CategoriesListUiState
  data class Error(val message: String) : CategoriesListUiState
  data class DisplayCategoriesList(val categories: ImmutableList<Category>) : CategoriesListUiState
}