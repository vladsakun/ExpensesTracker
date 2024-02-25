package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.data.api.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.data.api.DefaultTransactionTargetName
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.resourceValueOf
import com.emendo.expensestracker.model.ui.textValueOf

private fun CategoryEntity.asExternalModel(value: TextValue = textValueOf(this.name)): CategoryModel =
  CategoryModel(
    id = id,
    name = value,
    icon = IconModel.getById(iconId),
    color = com.emendo.expensestracker.model.ui.ColorModel.getById(colorId),
    type = CategoryType.getById(type),
    ordinalIndex = ordinalIndex,
  )

internal fun asExternalModel(category: CategoryEntity): CategoryModel {
  if (category.id == DefaultTransactionTargetIncomeId || category.id == DefaultTransactionTargetExpenseId) {
    return if (category.name == DefaultTransactionTargetName) {
      category.asExternalModel(resourceValueOf(R.string.uncategorized))
    } else {
      category.asExternalModel()
    }
  }

  return category.asExternalModel()
}