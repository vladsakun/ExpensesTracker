package com.emendo.expensestracker.core.app.base.manager

import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import kotlinx.coroutines.flow.Flow

interface CreateTransactionRepository {
  suspend fun init()

  fun getTarget(): Flow<TransactionTarget?>
  fun getTargetSnapshot(): TransactionTarget?
  fun setTarget(target: TransactionTarget)

  fun getSource(): Flow<TransactionSource?>
  fun getSourceSnapshot(): TransactionSource?
  fun setSource(source: TransactionSource)

  fun getDefaultTarget(transactionType: TransactionType): TransactionTarget

  fun isSelectSourceFlow(): Boolean
  fun startSelectSourceFlow()
  fun finishSelectSourceFlow()

  fun clear()
}