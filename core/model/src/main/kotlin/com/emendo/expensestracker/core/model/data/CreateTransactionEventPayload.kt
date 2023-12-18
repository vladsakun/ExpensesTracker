package com.emendo.expensestracker.core.model.data

import kotlinx.datetime.Instant
import java.math.BigDecimal

data class CreateTransactionEventPayload(
  val transactionId: Long?,
  val transactionValueFormatted: String,
  val transactionValue: BigDecimal,
  val note: String?,
  val date: Instant,
  val transactionType: Int,
)