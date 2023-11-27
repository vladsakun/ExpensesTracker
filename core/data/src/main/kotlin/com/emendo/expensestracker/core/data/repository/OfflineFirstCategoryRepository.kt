package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInLazyList
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.mapper.CategoryFullMapper
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryWithTransactions
import com.emendo.expensestracker.core.data.model.category.asExternalModel
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.database.dao.CategoryDao
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.core.database.model.CategoryFull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val DefaultTransactionTargetExpenseId = 1L
const val DefaultTransactionTargetIncomeId = 2L

class OfflineFirstCategoryRepository @Inject constructor(
  private val categoryDao: CategoryDao,
  private val categoryFullMapper: CategoryFullMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @ApplicationScope private val scope: CoroutineScope,
) : CategoryRepository {

  private val categoriesState: StateFlow<List<CategoryModel>> by lazy(LazyThreadSafetyMode.NONE) {
    categoryDao
      .getAll()
      .map { categoryEntities -> categoryEntities.map { it.asExternalModel() } }
      .stateInLazyList(scope)
  }

  private val categoriesWithTransactionState: StateFlow<List<CategoryWithTransactions>> by lazy(LazyThreadSafetyMode.NONE) {
    categoryDao
      .getCategoriesFull()
      .map { categoryFulls ->
        categoryFulls
          .filterNot(::isEmptyPrepopulatedCategory)
          .map { categoryFullMapper.map(it) }
      }
      .stateInLazyList(scope)
  }

  override val categories: Flow<List<CategoryModel>>
    get() = categoriesState

  override val categoriesWithTransactions: Flow<List<CategoryWithTransactions>>
    get() = categoriesWithTransactionState

  override fun getCategoriesSnapshot(): List<CategoryModel> =
    categoriesState.value

  override suspend fun upsertCategory(
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
  ) {
    withContext(ioDispatcher) {
      categoryDao.save(
        CategoryEntity(
          name = name,
          iconId = icon.id,
          colorId = color.id,
          type = type.id,
        )
      )
    }
  }

  override suspend fun deleteCategory(id: Long) {
    withContext(ioDispatcher) {
      categoryDao.deleteById(id)
    }
  }

  private fun isEmptyPrepopulatedCategory(category: CategoryFull): Boolean {
    return (category.category.id == DefaultTransactionTargetExpenseId ||
      category.category.id == DefaultTransactionTargetIncomeId) &&
      category.transactions.isEmpty()
  }
}