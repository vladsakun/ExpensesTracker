package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.mapper.CategoryFullMapper
import com.emendo.expensestracker.core.data.model.CategoryModel
import com.emendo.expensestracker.core.data.model.CategoryWithTransactions
import com.emendo.expensestracker.core.data.model.asEntity
import com.emendo.expensestracker.core.data.model.asExternalModel
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.database.dao.CategoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstCategoryRepository @Inject constructor(
  private val categoryDao: CategoryDao,
  private val categoryFullMapper: CategoryFullMapper,
) : CategoryRepository {

  override fun getCategories(): Flow<List<CategoryModel>> =
    categoryDao.getAll().map { categoryEntities ->
      categoryEntities.map { it.asExternalModel() }
    }

  override fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>> =
    categoryDao.getCategoriesFull().map { categoryFulls ->
      categoryFulls.map { categoryFullMapper.map(it) }
    }

  override suspend fun upsertCategory(categoryModel: CategoryModel) {
    withContext(Dispatchers.IO) {
      categoryDao.save(categoryModel.asEntity())
    }
  }

  override suspend fun deleteCategory(categoryModel: CategoryModel) {
    withContext(Dispatchers.IO) {
      categoryDao.delete(categoryModel.asEntity())
    }
  }
}