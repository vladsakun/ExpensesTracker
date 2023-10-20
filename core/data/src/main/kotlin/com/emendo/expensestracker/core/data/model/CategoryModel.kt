package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.database.model.CategoryEntity

data class CategoryModel(
  override val id: Long = 0,
  val name: String,
  val icon: IconModel,
  val color: ColorModel,
  val type: CategoryType,
) : TransactionTarget

fun CategoryEntity.asExternalModel(): CategoryModel {
  return CategoryModel(
    id = id,
    name = name,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
    type = CategoryType.getById(type),
  )
}

fun CategoryModel.asEntity(): CategoryEntity {
  return CategoryEntity(
    id = id,
    name = name,
    iconId = icon.id,
    colorId = color.id,
    type = type.id,
  )
}

fun CategoryModel.asTransactionUiModel(): CalculatorTransactionUiModel {
  return CalculatorTransactionUiModel(
    name = name,
    icon = icon.imageVector,
  )
}

enum class CategoryType(val id: Int) {
  EXPENSE(0),
  INCOME(1);

  companion object {
    fun getById(id: Int) = entries.first { it.id == id }
  }
}