package com.emendo.expensestracker.createtransaction.selectcategory

import com.emendo.expensestracker.core.data.model.category.CategoryModel
import kotlinx.collections.immutable.ImmutableList

sealed interface SelectCategoryUiState {
  data object Loading : SelectCategoryUiState
  data object Empty : SelectCategoryUiState
  data class Error(val message: String) : SelectCategoryUiState

  // Todo recomposes on transaction type switch. Override equals to avoid id comparison
  data class DisplayCategoryList(val categories: ImmutableList<CategoryModel>) : SelectCategoryUiState
}