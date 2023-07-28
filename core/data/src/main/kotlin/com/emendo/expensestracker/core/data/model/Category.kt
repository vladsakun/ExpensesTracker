package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.database.model.CategoryEntity

data class Category constructor(
  val id: Long = 0,
  val name: String,
  val icon: CategoryIconResource,
  val color: EntityColor,
)

fun CategoryEntity.toExternalModel(): Category = with(this) {
  Category(
    id = id,
    name = name,
    icon = CategoryIconResource.getById(iconId),
    color = EntityColor.getById(colorId),
  )
}

fun Category.asEntity(): CategoryEntity = with(this) {
  CategoryEntity(
    id = id,
    name = name,
    iconId = icon.id,
    colorId = color.id,
  )
}