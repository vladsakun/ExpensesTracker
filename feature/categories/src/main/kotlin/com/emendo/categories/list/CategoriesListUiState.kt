package com.emendo.categories.list

import com.emendo.expensestracker.core.data.model.Category

sealed interface CategoriesListUiState {
  object Loading: CategoriesListUiState
  data class Error(val message: String): CategoriesListUiState
  data class DisplayCategoriesList(val categories: List<CategoryItemType>): CategoriesListUiState
}

sealed class CategoryItemType {
  object AddCategoryItemType : CategoryItemType()
  data class CategoryItem(val category: Category) : CategoryItemType()
}