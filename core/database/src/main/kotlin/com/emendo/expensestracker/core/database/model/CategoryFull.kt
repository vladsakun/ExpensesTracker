package com.emendo.expensestracker.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryFull(
  @Embedded val category: CategoryEntity,
  @Relation(entity = TransactionEntity::class, parentColumn = "id", entityColumn = "targetId")
  val transactions: List<TransactionEntity>,
)