package com.emendo.expensestracker.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionFull(
  @Embedded
  val transactionEntity: TransactionEntity,
  @Relation(parentColumn = "sourceAccountId", entityColumn = "id")
  val sourceAccount: AccountEntity,
  @Relation(parentColumn = "targetAccountId", entityColumn = "id")
  val targetAccount: AccountEntity?,
  @Relation(parentColumn = "targetCategoryId", entityColumn = "id")
  val targetCategory: CategoryEntity?,
)