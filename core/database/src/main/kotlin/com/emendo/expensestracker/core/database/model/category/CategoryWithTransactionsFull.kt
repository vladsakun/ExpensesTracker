package com.emendo.expensestracker.core.database.model.category

import androidx.room.Embedded
import androidx.room.Relation
import com.emendo.expensestracker.core.database.model.transaction.TransactionEntity

data class CategoryWithTransactionsFull(
  @Embedded val category: CategoryFull,
  @Relation(parentColumn = "id", entityColumn = "targetCategoryId")
  val transactions: List<TransactionEntity>,
)