package com.emendo.expensestracker.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryFull(
  @Embedded val category: CategoryEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "targetCategoryId",
  )
  val transactions: List<TransactionEntity>,
)