package com.emendo.categories.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CategoriesListViewModel @Inject constructor(
  categoryRepository: CategoryRepository,
) : ViewModel() {

  private val _navigationChannel = Channel<Unit?>(Channel.CONFLATED)
  val navigationEvent: Flow<Unit?> = _navigationChannel.receiveAsFlow()

  val uiState: StateFlow<CategoriesListUiState> = categoriesUiState(categoryRepository)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = CategoriesListUiState.Loading
    )

  fun registerListener() {
  }
}

private fun categoriesUiState(categoryRepository: CategoryRepository): Flow<CategoriesListUiState> {
  return categoryRepository.getCategories().asResult().map { categoriesResult ->
    when (categoriesResult) {
      is Result.Success -> {
        val categoriesMutable: MutableList<CategoryItemType> = ArrayList<CategoryItemType>(
          categoriesResult.data.map {
            CategoryItemType.CategoryItem(it)
          }).apply {
          add(CategoryItemType.AddCategoryItemType)
        }

        CategoriesListUiState.DisplayCategoriesList(categoriesMutable)
      }

      is Result.Error -> {
        CategoriesListUiState.Error("Error")
      }

      is Result.Loading -> {
        CategoriesListUiState.Loading
      }
    }
  }
}