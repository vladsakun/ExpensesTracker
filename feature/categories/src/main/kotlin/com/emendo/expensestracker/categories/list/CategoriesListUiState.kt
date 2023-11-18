package com.emendo.expensestracker.categories.list

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface CategoriesListUiState {
  data object Loading : CategoriesListUiState
  data object Empty : CategoriesListUiState
  data class Error(val message: String) : CategoriesListUiState
  data class DisplayCategoriesList(
    val categories: ImmutableMap<Int, ImmutableList<CategoryWithTotalTransactions>>,
  ) : CategoriesListUiState
}

sealed interface BaseDialogListUiState<out T> {
  data object Loading : BaseDialogListUiState<Nothing>
  data class Error(val message: String) : BaseDialogListUiState<Nothing>
  data class DisplayList<T>(val data: T) : BaseDialogListUiState<T>
  data object Empty : BaseDialogListUiState<Nothing>
}

@Stable
sealed interface CategoriesListDialogData {
  data class Accounts(
    val accountModels: ImmutableList<AccountModel>,
    val onSelectAccount: (AccountModel) -> Unit,
  ) : CategoriesListDialogData

  data class Categories(
    val categories: ImmutableList<CategoryModel>,
    val onSelectCategory: (CategoryModel) -> Unit,
  ) : CategoriesListDialogData
}