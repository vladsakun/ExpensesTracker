package com.emendo.expensestracker.createtransaction.transaction.data

import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_TRANSFER_TRANSACTION
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.model.transaction.toTransactionType

internal fun getTransactionType(payload: CreateTransactionEventPayload?): TransactionType {
  if (IS_DEBUG_TRANSFER_TRANSACTION) {
    return TransactionType.TRANSFER
  }

  return payload?.transactionType?.toTransactionType() ?: TransactionType.DEFAULT
}