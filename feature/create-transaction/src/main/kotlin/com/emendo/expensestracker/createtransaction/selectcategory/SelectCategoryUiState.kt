package com.emendo.expensestracker.createtransaction.selectcategory

import com.emendo.expensestracker.data.api.model.category.CategoryModel
import kotlinx.collections.immutable.ImmutableList

sealed interface SelectCategoryUiState {
  data object Loading : SelectCategoryUiState
  data object Empty : SelectCategoryUiState
  data class Error(val message: String) : SelectCategoryUiState
  data class DisplayCategoryList(val categories: ImmutableList<CategoryModel>) : SelectCategoryUiState
}