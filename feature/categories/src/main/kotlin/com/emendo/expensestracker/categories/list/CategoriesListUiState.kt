package com.emendo.expensestracker.categories.list

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.categories.list.model.CategoryWithTotal
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.data.api.model.category.CategoryWithTotalTransactions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface CategoriesListState {
  data object Loading : CategoriesListState
  data object Empty : CategoriesListState
  data class Error(val message: String) : CategoriesListState
  data class DisplayCategoriesList(
    val tabs: ImmutableList<TabData>,
    val categories: Map<Int, List<CategoryWithTotalTransactions>>,
  ) : CategoriesListState
}

sealed interface CategoriesListUiState {
  data object Loading : CategoriesListUiState
  data object Empty : CategoriesListUiState
  data class Error(val message: String) : CategoriesListUiState
  data class DisplayCategoriesList(
    val tabs: ImmutableList<TabData>,
    val categories: ImmutableMap<Int, CategoriesList>,
  ) : CategoriesListUiState
}

val CategoriesListState.successValue: CategoriesListState.DisplayCategoriesList?
  get() = this as? CategoriesListState.DisplayCategoriesList

@Stable
data class CategoriesList(
  val dataList: ImmutableList<CategoryWithTotal>,
) {
  override fun equals(other: Any?): Boolean {
    if (javaClass != other?.javaClass) return false

    other as CategoriesList

    return (dataList.size == other.dataList.size) && dataList.containsAll(other.dataList)
  }

  override fun hashCode(): Int {
    return dataList.hashCode()
  }
}