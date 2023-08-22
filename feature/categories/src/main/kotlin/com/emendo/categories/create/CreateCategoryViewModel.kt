package com.emendo.categories.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.data.model.Category
import com.emendo.expensestracker.core.data.model.CategoryIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCategoryViewModel @Inject constructor(
  private val categoryRepository: CategoryRepository,
) : ViewModel() {

  var categoryName: String? = null

  private fun createCategory(categoryName: String) {
    viewModelScope.launch {
      categoryRepository.upsertCategory(
        Category(
          name = categoryName,
          icon = CategoryIconModel.EDUCATION,
          color = ColorModel.BLACK
        )
      )
    }
  }

  fun registerListener() {
  }
}