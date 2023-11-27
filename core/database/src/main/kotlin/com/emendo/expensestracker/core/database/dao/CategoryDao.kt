package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.core.database.model.CategoryFull
import com.emendo.expensestracker.core.database.util.CATEGORY_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.TABLE_CATEGORY
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao : BaseDao<CategoryEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract fun getById(id: Long): Flow<CategoryEntity>

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME")
  abstract fun getCategoriesFull(): Flow<List<CategoryFull>>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE category.type = :categoryType")
  abstract fun getCategoriesFullByType(categoryType: Int): Flow<List<CategoryFull>>

  @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
  abstract fun deleteById(id: Long)

  companion object {
    private const val TABLE_NAME = TABLE_CATEGORY
    private const val PRIMARY_KEY = CATEGORY_PRIMARY_KEY
  }
}