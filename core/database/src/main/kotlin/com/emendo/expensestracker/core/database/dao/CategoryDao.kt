package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.category.*
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
  abstract fun getCategoriesWithTransactionsFull(): Flow<List<CategoryWithTransactionsFull>>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE category.type = :categoryType")
  abstract fun getCategoriesFullByType(categoryType: Int): Flow<List<CategoryWithTransactionsFull>>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME")
  abstract fun getCategoriesFull(): Flow<List<CategoryFull>>

  @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
  abstract suspend fun deleteById(id: Long)

  @Update(entity = CategoryEntity::class)
  abstract suspend fun updateCategoryDetail(update: CategoryDetailUpdate)

  @Update(entity = CategoryEntity::class)
  abstract suspend fun updateOrdinalIndex(update: CategoryOrdinalIndexUpdate)

  companion object {
    private const val TABLE_NAME = TABLE_CATEGORY
    private const val PRIMARY_KEY = CATEGORY_PRIMARY_KEY
  }
}