package com.emendo.expensestracker.core.data.repository.api

import androidx.paging.PagingData
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.model.transaction.TransactionValueWithType
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import java.math.BigDecimal

interface TransactionRepository {

  val transactionsFull: Flow<List<TransactionModel>>
  val lastTransactionFull: Flow<TransactionModel?>
  fun getTransactionsPager(): Flow<PagingData<TransactionModel>>

  suspend fun retrieveLastTransactionFull(): TransactionModel?
  suspend fun retrieveTransaction(id: Long): TransactionModel?
  suspend fun retrieveTransactionsInPeriod(from: Instant, to: Instant): List<TransactionValueWithType>

  suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
    sourceCurrency: CurrencyModel? = source.currency,
    targetCurrency: CurrencyModel? = target.currency,
    transferAmount: BigDecimal? = null,
    note: String? = null,
  )

  suspend fun deleteTransaction(id: Long)
}