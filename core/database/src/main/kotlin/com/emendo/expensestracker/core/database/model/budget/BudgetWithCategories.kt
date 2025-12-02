package com.emendo.expensestracker.core.database.model.budget

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.emendo.expensestracker.core.database.model.category.CategoryEntity

data class BudgetWithCategories(
  @Embedded
  val budget: BudgetEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "id",
    associateBy = Junction(
      value = BudgetCategoryEntity::class,
      parentColumn = "budgetId",
      entityColumn = "categoryId",
    ),
  )
  val categories: List<CategoryEntity>,
)

