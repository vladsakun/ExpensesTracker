package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
  fun getCategories(): Flow<List<Category>>
  suspend fun upsertCategory(category: Category)
  suspend fun deleteCategory(category: Category)
}