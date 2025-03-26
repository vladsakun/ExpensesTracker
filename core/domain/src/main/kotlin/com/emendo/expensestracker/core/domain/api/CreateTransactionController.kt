package com.emendo.expensestracker.core.domain.api

import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import kotlinx.coroutines.flow.Flow

interface CreateTransactionController {
  fun getTarget(): Flow<TransactionTarget?>
  fun getTargetSnapshot(): TransactionTarget?
  fun setTarget(target: TransactionTarget?)

  fun getSource(): Flow<TransactionSource?>
  fun getSourceSnapshot(): TransactionSource?
  fun setSource(source: TransactionSource?)

  fun getDefaultNonTransferTarget(transactionType: TransactionType): TransactionTarget?

  fun getTransactionPayload(): CreateTransactionEventPayload?
  fun setTransactionPayload(newPayload: CreateTransactionEventPayload)

  fun clear(shouldClearTarget: Boolean)
}

fun CreateTransactionController.getTargetOrNonTransferDefault(transactionType: TransactionType): TransactionTarget? {
  return getTargetSnapshot() ?: getDefaultNonTransferTarget(transactionType)
}