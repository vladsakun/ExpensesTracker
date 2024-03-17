package com.emendo.expensestracker.app.base.api.screens

import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget

interface CreateTransactionScreenApi {
  fun openCreateTransactionScreen(
    source: TransactionSource?,
    target: TransactionTarget?,
    payload: CreateTransactionEventPayload?,
    shouldNavigateUp: Boolean,
  ): String
}