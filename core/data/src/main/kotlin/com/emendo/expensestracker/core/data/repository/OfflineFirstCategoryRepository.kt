package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.ext.stateInEagerlyList
import com.emendo.expensestracker.core.app.common.ext.stateInLazilyList
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.mapper.CategoryFullMapper
import com.emendo.expensestracker.core.data.mapper.asExternalModel
import com.emendo.expensestracker.core.database.dao.CategoryDao
import com.emendo.expensestracker.core.database.model.category.CategoryDetailUpdate
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.model.category.CategoryOrdinalIndexUpdate
import com.emendo.expensestracker.data.api.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetOrdinalIndex
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryWithTransactions
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.resourceValueOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstCategoryRepository @Inject constructor(
  private val categoryDao: CategoryDao,
  private val categoryFullMapper: CategoryFullMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @ApplicationScope private val scope: CoroutineScope,
) : CategoryRepository {

  private val categoriesState: StateFlow<List<CategoryModel>> =
    categoryDao
      .getCategoriesFull()
      .map { categories -> categories.map(::asExternalModel) }
      .stateInEagerlyList(scope)

  private val categoriesWithTransactionState: StateFlow<List<CategoryWithTransactions>> by lazy(LazyThreadSafetyMode.NONE) {
    categoryDao
      .getCategoriesWithTransactionsFull()
      .map { categoryFulls -> categoryFulls.map { categoryFullMapper.map(it) } }
      .stateInLazilyList(scope)
  }

  init {
    createDefaultCategories()
  }

  override fun getCategories(): Flow<List<CategoryModel>> = categoriesState
  override fun getCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>> = categoriesWithTransactionState
  override fun getCategoriesSnapshot(): List<CategoryModel> = categoriesState.value
  override fun getCategorySnapshotById(id: Long): CategoryModel? = categoriesState.value.firstOrNull { it.id == id }

  override suspend fun createCategory(
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
  ): Long {
    return withContext(ioDispatcher) {
      val categoriesByType = getCategoriesByType(type)
      val ordinalIndex = getMaxOrdinalIndex(categoriesByType)
      categoryDao.save(
        CategoryEntity(
          name = name,
          iconId = icon.id,
          colorId = color.id,
          type = type.id,
          ordinalIndex = ordinalIndex,
        )
      )
    }
  }

  private fun getMaxOrdinalIndex(categoriesByType: List<CategoryModel>): Int {
    var maxOrdinalIndex = -1
    categoriesByType.forEach {
      val categoryOrdinalIndex = it.ordinalIndex
      if (categoryOrdinalIndex == DefaultTransactionTargetOrdinalIndex) {
        return@forEach
      }

      if (categoryOrdinalIndex > maxOrdinalIndex) {
        maxOrdinalIndex = categoryOrdinalIndex
      }
    }
    return maxOrdinalIndex + 1
  }

  override suspend fun updateCategory(
    id: Long,
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
  ) {
    withContext(ioDispatcher) {
      categoryDao.updateCategoryDetail(
        CategoryDetailUpdate(
          id = id,
          name = name,
          iconId = icon.id,
          colorId = color.id,
          type = type.id,
        )
      )
    }
  }

  override suspend fun updateOrdinalIndex(id: Long, ordinalIndex: Int) {
    withContext(ioDispatcher) {
      categoryDao.updateOrdinalIndex(
        CategoryOrdinalIndexUpdate(
          id = id,
          ordinalIndex = ordinalIndex,
        )
      )
    }
  }

  override suspend fun deleteCategory(id: Long) {
    withContext(ioDispatcher) {
      categoryDao.deleteById(id)
    }
  }

  private fun getCategoriesByType(type: CategoryType) = categoriesState.value.filter { it.type == type }

  // Todo remove
  private fun getDefaultTarget(transactionType: CategoryType): TransactionTarget =
    CategoryModel(
      id = if (transactionType == CategoryType.EXPENSE) {
        DefaultTransactionTargetExpenseId
      } else {
        DefaultTransactionTargetIncomeId
      },
      name = resourceValueOf(R.string.uncategorized),
      icon = IconModel.UNKNOWN,
      color = ColorModel.Base,
      type = if (transactionType == CategoryType.EXPENSE) {
        CategoryType.EXPENSE
      } else {
        CategoryType.INCOME
      },
      ordinalIndex = DefaultTransactionTargetOrdinalIndex,
      subcategories = emptyList(),
    )

  private fun createDefaultCategories() {
    scope.launch(ioDispatcher) {
      val expenseTarget = getDefaultTarget(CategoryType.EXPENSE)
      val incomeTarget = getDefaultTarget(CategoryType.INCOME)
      with(expenseTarget) {
        categoryDao.save(
          CategoryEntity(
            id = id,
            name = "",
            iconId = icon.id,
            colorId = color.id,
            type = CategoryType.EXPENSE.id,
            ordinalIndex = ordinalIndex,
          )
        )
      }

      with(incomeTarget) {
        categoryDao.save(
          CategoryEntity(
            id = id,
            name = "",
            iconId = icon.id,
            colorId = color.id,
            type = CategoryType.INCOME.id,
            ordinalIndex = ordinalIndex,
          )
        )
      }
    }
  }
}