package com.emendo.categories.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.TopAppBarActionClickEventBus
import com.emendo.expensestracker.core.data.model.Category
import com.emendo.expensestracker.core.data.model.CategoryIconResource
import com.emendo.expensestracker.core.data.model.EntityColor
import com.emendo.expensestracker.core.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val topAppBarActionClickEventBus: TopAppBarActionClickEventBus,
) : ViewModel() {

  var categoryName: String? = null

  private fun createCategory(categoryName: String) {
    viewModelScope.launch {
      categoryRepository.upsertCategory(
        Category(
          name = categoryName,
          icon = CategoryIconResource.EDUCATION,
          color = EntityColor.BLACK
        )
      )
    }
  }

  fun registerListener() {
    topAppBarActionClickEventBus.registeredCallback = {
      categoryName?.let {
        createCategory(it)
      }
    }
  }
}