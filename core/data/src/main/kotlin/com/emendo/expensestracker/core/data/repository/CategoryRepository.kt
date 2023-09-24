package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.model.Category
import com.emendo.expensestracker.core.data.model.CategoryWithTransactions
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
  fun getCategories(): Flow<List<Category>>
  fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>>
  suspend fun upsertCategory(category: Category)
  suspend fun deleteCategory(category: Category)
}