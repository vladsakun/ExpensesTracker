package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.database.util.TABLE_TRANSACTION
import com.emendo.expensestracker.core.database.util.TRANSACTION_PRIMARY_KEY
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransactionDao : BaseDao<TransactionEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract fun getById(id: Long): Flow<TransactionEntity>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME")
  abstract fun getTransactionFull(): Flow<List<TransactionFull>>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME ORDER BY id LIMIT 1")
  abstract fun getLastTransaction(): Flow<TransactionFull?>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME ORDER BY id LIMIT 1")
  abstract suspend fun retrieveLastTransaction(): TransactionFull?

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  @Upsert
  abstract fun upsert(transaction: TransactionEntity)

  companion object {
    private const val TABLE_NAME = "`$TABLE_TRANSACTION`"
    private const val PRIMARY_KEY = TRANSACTION_PRIMARY_KEY
  }

}