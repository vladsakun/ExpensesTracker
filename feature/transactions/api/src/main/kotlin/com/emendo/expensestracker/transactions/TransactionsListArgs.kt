package com.emendo.expensestracker.transactions

import com.emendo.expensestracker.core.model.data.TransactionType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TransactionsListArgs private constructor(
  val categoryId: Long?,
  val from: Instant,
  val to: Instant,
  val transactionType: TransactionType?,
) {

  constructor(categoryId: Long, from: Instant, to: Instant) : this(
    categoryId = categoryId,
    from = from,
    to = to,
    transactionType = null,
  )

  constructor(transactionType: TransactionType, from: Instant, to: Instant) : this(
    categoryId = null,
    from = from,
    to = to,
    transactionType = transactionType,
  )
}