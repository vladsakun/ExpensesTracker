package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInEagerlyList
import com.emendo.expensestracker.core.app.common.ext.stateInLazilyList
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.resourceValueOf
import com.emendo.expensestracker.core.data.mapper.CategoryFullMapper
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryWithTransactions
import com.emendo.expensestracker.core.data.model.category.asExternalModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.database.dao.CategoryDao
import com.emendo.expensestracker.core.database.model.CategoryDetailUpdate
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.core.database.model.CategoryOrdinalIndexUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val DefaultTransactionTargetExpenseId = 1L
const val DefaultTransactionTargetIncomeId = 2L
const val DefaultTransactionTargetOrdinalIndex = Int.MAX_VALUE
const val DefaultTransactionTargetName = ""

class OfflineFirstCategoryRepository @Inject constructor(
  private val categoryDao: CategoryDao,
  private val categoryFullMapper: CategoryFullMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @ApplicationScope private val scope: CoroutineScope,
) : CategoryRepository {

  private val categoriesState: StateFlow<List<CategoryModel>> =
    categoryDao
      .getAll()
      .map { categories -> categories.map(::asExternalModel) }
      .stateInEagerlyList(scope)

  private val categoriesWithTransactionState: StateFlow<List<CategoryWithTransactions>> by lazy(LazyThreadSafetyMode.NONE) {
    categoryDao
      .getCategoriesFull()
      .map { categoryFulls -> categoryFulls.map { categoryFullMapper.map(it) } }
      .stateInLazilyList(scope)
  }

  override val categories: Flow<List<CategoryModel>>
    get() = categoriesState

  override val categoriesWithTransactions: Flow<List<CategoryWithTransactions>>
    get() = categoriesWithTransactionState

  override val categoriesSnapshot: List<CategoryModel>
    get() = categoriesState.value

  override suspend fun createCategory(
    name: String,
    icon: IconModel,
    color: ColorModel,
    type: CategoryType,
  ) {
    withContext(ioDispatcher) {
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

  private fun getCategoriesByType(type: CategoryType) =
    categoriesState.value.filter { it.type == type }

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