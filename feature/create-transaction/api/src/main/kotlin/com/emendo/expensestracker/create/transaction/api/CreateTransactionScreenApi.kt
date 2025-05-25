package com.emendo.expensestracker.create.transaction.api

import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget

interface CreateTransactionScreenApi {

  fun getRoute(
    source: TransactionSource?,
    target: TransactionTarget?,
    payload: CreateTransactionEventPayload? = null,
  ): String
}
