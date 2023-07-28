package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.model.Category
import com.emendo.expensestracker.core.data.model.asEntity
import com.emendo.expensestracker.core.data.model.toExternalModel
import com.emendo.expensestracker.core.database.dao.CategoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstCategoryRepository @Inject constructor(
  private val categoryDao: CategoryDao,
) : CategoryRepository {

  override fun getCategories(): Flow<List<Category>> {
    return categoryDao.getAll().map { categoryEntities ->
      categoryEntities.map { it.toExternalModel() }
    }
  }

  override suspend fun upsertCategory(category: Category) {
    withContext(Dispatchers.IO) {
      categoryDao.save(category.asEntity())
    }
  }

  override suspend fun deleteCategory(category: Category) {
    withContext(Dispatchers.IO) {
      categoryDao.delete(category.asEntity())
    }
  }
}