package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.data.model.TransactionModel
import com.emendo.expensestracker.core.data.model.TransactionSource
import com.emendo.expensestracker.core.data.model.TransactionTarget
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface TransactionsRepository {

  fun getTransactionsFull(): Flow<List<TransactionModel>>

  suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
    sourceCurrency: CurrencyModel? = source.currency,
    targetCurrency: CurrencyModel? = target.currency,
    transferAmount: BigDecimal? = null,
  )
}