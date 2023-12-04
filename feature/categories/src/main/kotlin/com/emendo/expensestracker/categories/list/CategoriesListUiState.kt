package com.emendo.expensestracker.categories.list

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface CategoriesListUiState {
  data object Loading : CategoriesListUiState
  data object Empty : CategoriesListUiState
  data class Error(val message: String) : CategoriesListUiState

  @Stable
  data class DisplayCategoriesList(
    val tabs: ImmutableList<TabData>,
    val categories: ImmutableMap<Int, ImmutableList<CategoryWithTotal>>,
    val isEditMode: Boolean = false,
  ) : CategoriesListUiState
}

@Stable
data class CategoryWithTotal(
  val categoryModel: CategoryModel,
  val totalFormatted: String,
)