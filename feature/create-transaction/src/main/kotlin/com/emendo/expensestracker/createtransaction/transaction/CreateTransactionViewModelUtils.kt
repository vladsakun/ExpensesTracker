package com.emendo.expensestracker.createtransaction.transaction

import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_TRANSFER_TRANSACTION
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.data.model.transaction.TransactionType.Companion.toTransactionType
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload

internal fun getTransactionType(payload: CreateTransactionEventPayload?): TransactionType {
  if (IS_DEBUG_TRANSFER_TRANSACTION) {
    return TransactionType.TRANSFER
  }

  return payload?.transactionType?.toTransactionType() ?: TransactionType.DEFAULT
}