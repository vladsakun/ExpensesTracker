package com.emendo.expensestracker.core.data.model.category

import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
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

private fun CategoryEntity.toExternalModel(value: TextValue = TextValue.Value(this.name)): CategoryModel {
  return CategoryModel(
    id = id,
    name = value,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
    type = CategoryType.getById(type),
  )
}

fun asExternalModel(category: CategoryEntity) =
  if (category.id == DefaultTransactionTargetIncomeId || category.id == DefaultTransactionTargetExpenseId) {
    category.toExternalModel(TextValue.Resource(R.string.uncategorized))
  } else {
    category.toExternalModel()
  }