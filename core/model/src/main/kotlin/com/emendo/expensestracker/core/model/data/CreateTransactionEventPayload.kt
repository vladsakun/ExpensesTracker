package com.emendo.expensestracker.core.model.data

import kotlinx.datetime.Instant

data class CreateTransactionEventPayload(
  val transactionId: Long?,
  val transactionAmount: Amount,
  val note: String?,
  val date: Instant,
  val transactionType: Int,
  val transferReceivedAmount: Amount?,
)