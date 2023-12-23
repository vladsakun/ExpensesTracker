package com.emendo.expensestracker.core.data.repository.api

import androidx.paging.PagingData
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.model.transaction.TransactionValueWithType
import com.emendo.expensestracker.core.model.data.Amount
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TransactionRepository {

  val transactionsPagingFlow: Flow<PagingData<TransactionModel>>
  fun getLastTransactionFull(): Flow<TransactionModel?>

  suspend fun retrieveLastTransferTransaction(sourceAccountId: Long): TransactionModel?
  suspend fun retrieveTransactionsInPeriod(from: Instant, to: Instant): List<TransactionValueWithType>

  suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: Amount,
    transferReceivedAmount: Amount? = null,
    note: String? = null,
  )

  suspend fun deleteTransaction(id: Long)
}