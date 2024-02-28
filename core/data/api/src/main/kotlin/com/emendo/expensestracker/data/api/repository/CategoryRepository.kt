package com.emendo.expensestracker.data.api.repository

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryWithTransactions
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
  fun getCategories(): Flow<List<CategoryModel>>
  fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>>
  fun getCategoriesSnapshot(): List<CategoryModel>
  fun getById(id: Long): Flow<CategoryModel>

  suspend fun createCategory(
    name: String,
    icon: IconModel,
    color: com.emendo.expensestracker.model.ui.ColorModel,
    type: CategoryType,
  )

  suspend fun updateCategory(
    id: Long,
    name: String,
    icon: IconModel,
    color: com.emendo.expensestracker.model.ui.ColorModel,
    type: CategoryType,
  )

  suspend fun updateOrdinalIndex(id: Long, ordinalIndex: Int)
  suspend fun deleteCategory(id: Long)
}