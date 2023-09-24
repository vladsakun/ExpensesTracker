package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.model.TransactionModel
import com.emendo.expensestracker.core.model.data.TransactionSource
import com.emendo.expensestracker.core.model.data.TransactionTarget
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface TransactionRepository {

  fun getTransactionsFull(): Flow<List<TransactionModel>>

  suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
  )
}