package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryWithTransactions
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
  fun getCategories(): Flow<List<CategoryModel>>
  fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>>
  suspend fun upsertCategory(categoryModel: CategoryModel)
  suspend fun deleteCategory(categoryModel: CategoryModel)
}