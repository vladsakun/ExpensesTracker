package com.emendo.expensestracker.core.database.model.category

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryFull(
  @Embedded
  val categoryEntity: CategoryEntity,
  @Relation(parentColumn = "id", entityColumn = "categoryId")
  val subCategories: List<SubcategoryEntity>,
)