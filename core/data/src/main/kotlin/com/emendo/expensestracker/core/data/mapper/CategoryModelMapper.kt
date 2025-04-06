package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.model.category.CategoryFull
import com.emendo.expensestracker.core.database.model.category.SubcategoryEntity
import com.emendo.expensestracker.data.api.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetName
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.resourceValueOf
import com.emendo.expensestracker.model.ui.textValueOf

internal fun asExternalModel(category: CategoryFull): CategoryModel {
  return category.asExternalModel(category.nameOrDefault())
}

internal fun CategoryEntity.asExternalModel(): CategoryModel {
  return asExternalModel(nameOrDefault())
}

internal fun SubcategoryEntity.asExternalModel(category: CategoryEntity): CategoryModel {
  val categoryModel = category.asExternalModel()

  val target = CategoryModel(
    id = id,
    icon = IconModel.getById(iconId),
    name = textValueOf(name),
    color = categoryModel.color,
    ordinalIndex = ordinalIndex,
    subcategories = emptyList(),
    type = CategoryType.getById(category.type),
  )

  return target
}

private fun CategoryFull.asExternalModel(name: TextValue): CategoryModel =
  CategoryModel(
    id = categoryEntity.id,
    name = name,
    icon = IconModel.getById(categoryEntity.iconId),
    color = ColorModel.getById(categoryEntity.colorId),
    type = CategoryType.getById(categoryEntity.type),
    ordinalIndex = categoryEntity.ordinalIndex,
    subcategories = subCategories.map { it.asExternalModel(categoryEntity) },
  )

private fun CategoryEntity.asExternalModel(name: TextValue): CategoryModel =
  CategoryModel(
    id = id,
    name = name,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
    type = CategoryType.getById(type),
    ordinalIndex = ordinalIndex,
    subcategories = emptyList(),
  )

private fun CategoryFull.nameOrDefault(): TextValue = categoryEntity.nameOrDefault()

private fun CategoryEntity.nameOrDefault(): TextValue {
  if ((id == DefaultTransactionTargetIncomeId || id == DefaultTransactionTargetExpenseId) && name == DefaultTransactionTargetName) {
    return resourceValueOf(R.string.uncategorized)
  }

  return textValueOf(name)
}