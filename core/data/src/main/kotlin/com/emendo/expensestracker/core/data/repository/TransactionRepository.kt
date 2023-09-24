package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.model.data.TransactionSource
import com.emendo.expensestracker.core.model.data.TransactionTarget
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface TransactionRepository {

  fun getTransactions(): Flow<List<TransactionEntity>>
  fun getAllTransactions(): Flow<List<TransactionFull>>

  suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
  )
}