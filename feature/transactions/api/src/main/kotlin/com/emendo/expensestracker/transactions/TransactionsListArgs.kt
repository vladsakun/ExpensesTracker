package com.emendo.expensestracker.transactions

import com.emendo.expensestracker.core.model.data.TransactionType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed class TransactionsListArgs {

  abstract val from: Instant
  abstract val to: Instant

  @Serializable
  data class TransactionListArgsByCategory(
    val categoryId: Long,
    override val from: Instant,
    override val to: Instant,
  ) : TransactionsListArgs()

  @Serializable
  data class TransactionListArgsBySubcategory(
    val subcategoryId: Long,
    override val from: Instant,
    override val to: Instant,
  ) : TransactionsListArgs()

  @Serializable
  data class TransactionListArgsByType(
    val transactionType: TransactionType,
    override val from: Instant,
    override val to: Instant,
  ) : TransactionsListArgs()
}