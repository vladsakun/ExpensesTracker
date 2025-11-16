package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.budget.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BudgetDao : BaseDao<BudgetEntity>() {

  @Query("SELECT * FROM budget")
  abstract override fun getAll(): Flow<List<BudgetEntity>>

  @Query("SELECT * FROM budget WHERE id = :id")
  abstract fun getById(id: Long): Flow<BudgetEntity>

  @Query("SELECT * FROM budget WHERE id = :id")
  abstract suspend fun retrieveById(id: Long): BudgetEntity?

  @Query("DELETE FROM budget")
  abstract override suspend fun deleteAll()

  @Query("DELETE FROM budget WHERE id = :id")
  abstract fun deleteById(id: Long)

  @Update(entity = BudgetEntity::class)
  abstract suspend fun updateBudget(budget: BudgetEntity)
}
