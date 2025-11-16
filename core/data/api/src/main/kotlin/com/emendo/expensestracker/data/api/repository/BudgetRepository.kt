package com.emendo.expensestracker.data.api.repository

import com.emendo.expensestracker.core.model.data.BudgetPeriod
import com.emendo.expensestracker.data.api.model.BudgetModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface BudgetRepository {
  fun getBudgets(): Flow<List<BudgetModel>>
  fun getBudgetsSnapshot(): List<BudgetModel>
  fun getById(id: Long): Flow<BudgetModel>
  fun getByIdSnapshot(id: Long): BudgetModel?

  suspend fun createBudget(
    name: String,
    iconId: Int,
    colorId: Int,
    amount: BigDecimal,
    period: BudgetPeriod,
    categoryId: Long,
    currencyCode: String,
  )

  suspend fun updateBudget(
    id: Long,
    name: String,
    iconId: Int,
    colorId: Int,
    amount: BigDecimal,
    period: BudgetPeriod,
    categoryId: Long? = null,
    currencyCode: String,
  )

  suspend fun deleteBudget(id: Long)
}
