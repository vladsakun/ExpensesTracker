package com.emendo.expensestracker.core.data.model.category

import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.*
import com.emendo.expensestracker.core.data.repository.DefaultTransactionTargetName
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.DefaultTransactionTargetExpenseId
import com.emendo.expensestracker.data.api.repository.DefaultTransactionTargetIncomeId

data class CategoryModelImpl(
  override val id: Long = 0,
  override val name: TextValue,
  override val icon: IconModel,
  override val color: ColorModel,
  override val ordinalIndex: Int,
  override val type: CategoryType,
  override val currency: CurrencyModel? = null,
) : CategoryModel

private fun CategoryEntity.asExternalModel(value: TextValue = textValueOf(this.name)): CategoryModel =
  CategoryModelImpl(
    id = id,
    name = value,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
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