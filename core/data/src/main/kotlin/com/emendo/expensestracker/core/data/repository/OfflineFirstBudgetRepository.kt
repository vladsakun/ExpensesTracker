package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInLazily
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.mapper.BudgetMapper
import com.emendo.expensestracker.core.database.dao.BudgetDao
import com.emendo.expensestracker.core.database.model.budget.BudgetEntity
import com.emendo.expensestracker.core.model.data.BudgetPeriod
import com.emendo.expensestracker.data.api.model.BudgetModel
import com.emendo.expensestracker.data.api.repository.BudgetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class OfflineFirstBudgetRepository @Inject constructor(
  private val budgetDao: BudgetDao,
  private val budgetMapper: BudgetMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @ApplicationScope private val scope: CoroutineScope,
) : BudgetRepository {

  private val budgetsList: StateFlow<List<BudgetModel>> =
    budgetDao.getAll()
      .map { budgets -> budgets.map { budgetMapper.map(it) } }
      .stateInLazily(scope, emptyList())

  override fun getBudgets(): Flow<List<BudgetModel>> = budgetsList
  override fun getBudgetsSnapshot(): List<BudgetModel> = budgetsList.value

  override fun getById(id: Long): Flow<BudgetModel> = budgetDao
    .getById(id)
    .map { budgetMapper.map(it) }

  override fun getByIdSnapshot(id: Long): BudgetModel? =
    getBudgetsSnapshot().find { it.id == id }

  override suspend fun createBudget(
    name: String,
    iconId: Int,
    colorId: Int,
    amount: BigDecimal,
    period: BudgetPeriod,
    categoryId: Long,
    currencyCode: String,
  ) {
    withContext(ioDispatcher) {
      budgetDao.save(
        BudgetEntity(
          name = name,
          limit = amount,
          iconId = iconId,
          colorId = colorId,
          period = period,
          categoryId = categoryId,
          currencyCode = currencyCode,
        )
      )
    }
  }

  override suspend fun updateBudget(
    id: Long,
    name: String,
    iconId: Int,
    colorId: Int,
    amount: BigDecimal,
    period: BudgetPeriod,
    categoryId: Long?,
    currencyCode: String,
  ) {
    withContext(ioDispatcher) {
      budgetDao.updateBudget(
        BudgetEntity(
          id = id,
          name = name,
          limit = amount,
          iconId = iconId,
          colorId = colorId,
          period = period,
          categoryId = categoryId ?: 0L,
          currencyCode = currencyCode,
        )
      )
    }
  }

  override suspend fun deleteBudget(id: Long) {
    withContext(ioDispatcher) {
      budgetDao.deleteById(id)
    }
  }
}
