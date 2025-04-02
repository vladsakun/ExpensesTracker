package com.emendo.expensestracker.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.database.util.TABLE_TRANSACTION
import com.emendo.expensestracker.core.database.util.TRANSACTION_PRIMARY_KEY
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.model.data.TransactionType.Companion.id
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

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
  @Query("SELECT * FROM $TABLE_NAME ORDER BY date DESC LIMIT 1")
  abstract fun getLastTransaction(): Flow<TransactionFull?>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract suspend fun retrieveTransactionById(id: Long): TransactionFull?

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME ORDER BY date DESC")
  abstract fun transactionsPagingSource(): PagingSource<Int, TransactionFull>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE typeId == :typeId AND date BETWEEN :from AND :to ORDER BY date DESC")
  abstract fun transactionsByTypeInPeriodPagingSource(
    typeId: Int,
    from: Instant,
    to: Instant,
  ): PagingSource<Int, TransactionFull>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE targetCategoryId = :targetCategoryId AND date BETWEEN :from AND :to ORDER BY date DESC")
  abstract fun transactionsInPeriodPagingSource(
    targetCategoryId: Long,
    from: Instant,
    to: Instant,
  ): PagingSource<Int, TransactionFull>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME ORDER BY date DESC LIMIT 1")
  abstract suspend fun retrieveLastTransaction(): TransactionFull?

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE date BETWEEN :from AND :to")
  abstract fun getTransactionsInPeriod(from: Instant, to: Instant): Flow<List<TransactionFull>>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE date BETWEEN :from AND :to")
  abstract suspend fun retrieveTransactionsInPeriod(from: Instant, to: Instant): List<TransactionFull>

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME WHERE sourceAccountId = :sourceAccountId AND typeId = :typeId  ORDER BY date DESC LIMIT 1")
  abstract suspend fun retrieveLastTransferTransaction(
    sourceAccountId: Long,
    typeId: Int = TransactionType.TRANSFER.id,
  ): TransactionFull?

  @Transaction
  @Query("SELECT * FROM $TABLE_NAME ORDER BY date ASC LIMIT 1")
  abstract suspend fun retrieveFirstTransaction(): TransactionFull?

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  @Upsert
  abstract fun upsert(transaction: TransactionEntity)

  @Query("DELETE FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract suspend fun deleteById(id: Long)

  companion object {
    private const val TABLE_NAME = "`$TABLE_TRANSACTION`"
    private const val PRIMARY_KEY = TRANSACTION_PRIMARY_KEY
  }

}