package com.emendo.expensestracker.data.api.repository

import androidx.paging.PagingData
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.model.transaction.TransactionValueWithType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TransactionRepository {

  fun getTransactionsPagingFlow(cacheScope: CoroutineScope): Flow<PagingData<TransactionModel>>
  fun getLastTransactionFull(): Flow<TransactionModel?>

  suspend fun retrieveLastTransferTransaction(sourceAccountId: Long): TransactionModel?
  suspend fun retrieveFirstTransaction(): TransactionModel?
  suspend fun retrieveTransactionsInPeriod(from: Instant, to: Instant): List<TransactionValueWithType>
  suspend fun retrieveTransactionsByTypeInPeriod(
    transactionType: TransactionType,
    from: Instant,
    to: Instant,
  ): List<TransactionValueWithType>

  suspend fun retrieveTransactionsByCategoryInPeriod(
    categoryId: Long,
    from: Instant,
    to: Instant,
  ): List<TransactionValueWithType>

  suspend fun retrieveTransactionsBySubcategoryInPeriod(
    subcategoryId: Long,
    from: Instant,
    to: Instant,
  ): List<TransactionValueWithType>

  fun getTransactionsInPeriod(from: Instant, to: Instant): Flow<List<TransactionModel>>
  fun getTransactionsPagedInPeriod(
    targetCategoryId: Long,
    from: Instant,
    to: Instant,
  ): Flow<PagingData<TransactionModel>>

  fun getTransactionsInSubcategoryPagedInPeriod(
    targetSubcategoryId: Long,
    from: Instant,
    to: Instant,
  ): Flow<PagingData<TransactionModel>>

  fun getTransactionsInPeriod(
    targetCategoryId: Long,
    from: Instant,
    to: Instant,
  ): Flow<List<TransactionModel>>

  fun getTransactionsByTypeInPeriod(
    transactionType: TransactionType,
    from: Instant,
    to: Instant,
  ): Flow<List<TransactionValueWithType>>

  fun getTransactionsPagedInPeriod(
    transactionType: TransactionType,
    from: Instant,
    to: Instant,
  ): Flow<PagingData<TransactionModel>>

  suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    subcategoryId: Long?,
    amount: Amount,
    transferReceivedAmount: Amount? = null,
    note: String? = null,
  )

  suspend fun deleteTransaction(id: Long)
}