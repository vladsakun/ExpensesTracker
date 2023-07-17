package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.database.common.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao : BaseDao<AccountEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<AccountEntity>>

  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract fun getById(id: String): Flow<AccountEntity>

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  companion object {
    private const val TABLE_NAME = "account"
    private const val PRIMARY_KEY = "id"
  }
}