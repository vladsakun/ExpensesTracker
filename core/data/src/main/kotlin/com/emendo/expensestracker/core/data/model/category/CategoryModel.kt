package com.emendo.expensestracker.core.data.model.category

import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.*
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.core.database.model.CategoryEntity

data class CategoryModel(
  override val id: Long = 0,
  override val name: TextValue,
  override val icon: IconModel,
  override val color: ColorModel,
  val type: CategoryType,
) : TransactionTarget

private fun CategoryEntity.asExternalModel(value: TextValue = textValueOf(this.name)): CategoryModel =
  CategoryModel(
    id = id,
    name = value,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
    type = CategoryType.getById(type),
  )

fun asExternalModel(category: CategoryEntity) =
  if (category.id == DefaultTransactionTargetIncomeId || category.id == DefaultTransactionTargetExpenseId) {
    if (category.name.isBlank()) category.asExternalModel(resourceValueOf(R.string.uncategorized)) else category.asExternalModel()
  } else {
    category.asExternalModel()
  }