package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao : BaseDao<CategoryEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract fun getById(id: String): Flow<CategoryEntity>

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  companion object {
    private const val TABLE_NAME = "categories"
    private const val PRIMARY_KEY = "id"
  }
}