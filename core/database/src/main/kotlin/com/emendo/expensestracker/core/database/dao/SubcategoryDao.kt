package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.model.category.SubcategoryDetailUpdate
import com.emendo.expensestracker.core.database.model.category.SubcategoryEntity
import com.emendo.expensestracker.core.database.model.category.SubcategoryOrdinalIndexUpdate
import com.emendo.expensestracker.core.database.util.SUBCATEGORY_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.TABLE_SUBCATEGORY
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubcategoryDao : BaseDao<SubcategoryEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<SubcategoryEntity>>

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
  abstract suspend fun deleteById(id: Long)

  @Update(entity = SubcategoryEntity::class)
  abstract suspend fun updateSubcategoryDetail(update: SubcategoryDetailUpdate)

  @Update(entity = CategoryEntity::class)
  abstract suspend fun updateOrdinalIndex(update: SubcategoryOrdinalIndexUpdate)

  @Upsert
  abstract suspend fun upsert(entity: SubcategoryEntity)

  companion object {
    private const val TABLE_NAME = TABLE_SUBCATEGORY
    private const val PRIMARY_KEY = SUBCATEGORY_PRIMARY_KEY
  }
}