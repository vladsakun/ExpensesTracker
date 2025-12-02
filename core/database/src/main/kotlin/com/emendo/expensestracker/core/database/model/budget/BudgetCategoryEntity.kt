package com.emendo.expensestracker.core.database.model.budget

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.util.TABLE_BUDGET_CATEGORY

@Entity(
  tableName = TABLE_BUDGET_CATEGORY,
  primaryKeys = ["budgetId", "categoryId"],
  foreignKeys = [
    ForeignKey(
      entity = BudgetEntity::class,
      parentColumns = ["id"],
      childColumns = ["budgetId"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = CategoryEntity::class,
      parentColumns = ["id"],
      childColumns = ["categoryId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("budgetId"),
    Index("categoryId"),
  ],
)
data class BudgetCategoryEntity(
  val budgetId: Long,
  val categoryId: Long,
)

