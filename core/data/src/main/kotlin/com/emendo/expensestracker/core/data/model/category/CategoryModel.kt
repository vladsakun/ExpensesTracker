package com.emendo.expensestracker.core.data.model.category

import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.*
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetIncomeId
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetName
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.core.model.data.CurrencyModel

data class CategoryModel(
  override val id: Long = 0,
  override val name: TextValue,
  override val icon: IconModel,
  override val color: ColorModel,
  override val ordinalIndex: Int,
  val type: CategoryType,
  override val currency: CurrencyModel? = null,
) : TransactionTarget

private fun CategoryEntity.asExternalModel(value: TextValue = textValueOf(this.name)): CategoryModel =
  CategoryModel(
    id = id,
    name = value,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
    type = CategoryType.getById(type),
    ordinalIndex = ordinalIndex,
  )

fun asExternalModel(category: CategoryEntity): CategoryModel {
  if (category.id == DefaultTransactionTargetIncomeId || category.id == DefaultTransactionTargetExpenseId) {
    return if (category.name == DefaultTransactionTargetName) {
      category.asExternalModel(resourceValueOf(R.string.uncategorized))
    } else {
      category.asExternalModel()
    }
  }

  return category.asExternalModel()
}