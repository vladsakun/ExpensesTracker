package com.emendo.expensestracker.data.api.repository

import androidx.paging.PagingData
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.model.transaction.TransactionValueWithType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TransactionRepository {

  val transactionsPagingFlow: Flow<PagingData<TransactionModel>>
  fun getLastTransactionFull(): Flow<TransactionModel?>

  suspend fun retrieveLastTransferTransaction(sourceAccountId: Long): TransactionModel?
  suspend fun retrieveFirstTransaction(): TransactionModel?
  suspend fun retrieveTransactionsInPeriod(from: Instant, to: Instant): List<TransactionValueWithType>
  fun getTransactionsInPeriod(from: Instant, to: Instant): Flow<List<TransactionModel>>

  suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: Amount,
    transferReceivedAmount: Amount? = null,
    note: String? = null,
  )

  suspend fun deleteTransaction(id: Long)
}