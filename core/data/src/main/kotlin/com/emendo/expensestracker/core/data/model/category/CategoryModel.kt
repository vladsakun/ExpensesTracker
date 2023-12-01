package com.emendo.expensestracker.core.data.model.category

import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TransactionElementName
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.core.database.model.CategoryEntity

data class CategoryModel(
  override val id: Long = 0,
  override val name: TransactionElementName,
  override val icon: IconModel,
  override val color: ColorModel,
  val type: CategoryType,
) : TransactionTarget

private fun CategoryEntity.toExternalModel(name: TransactionElementName = TransactionElementName.Name(this.name)): CategoryModel {
  return CategoryModel(
    id = id,
    name = name,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
    type = CategoryType.getById(type),
  )
}

fun asExternalModel(category: CategoryEntity) =
  if (category.id == DefaultTransactionTargetIncomeId || category.id == DefaultTransactionTargetExpenseId) {
    category.toExternalModel(TransactionElementName.NameStringRes(R.string.uncategorized))
  } else {
    category.toExternalModel()
  }