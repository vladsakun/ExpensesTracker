package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.core.model.data.TransactionTarget

data class Category(
  override val id: Long = 0,
  val name: String,
  val icon: IconModel,
  val color: ColorModel,
  val type: CategoryType,
  val currencyModel: CurrencyModel,
) : TransactionTarget

fun CategoryEntity.asExternalModel(): Category {
  return Category(
    id = id,
    name = name,
    icon = IconModel.getById(iconId),
    color = ColorModel.getById(colorId),
    type = CategoryType.getById(type),
    currencyModel = CurrencyModel.getById(currencyId),
  )
}

fun Category.asEntity(): CategoryEntity {
  return CategoryEntity(
    id = id,
    name = name,
    iconId = icon.id,
    colorId = color.id,
    type = type.id,
    currencyId = currencyModel.id,
  )
}

fun Category.asTransactionUiModel(): CalculatorTransactionUiModel {
  return CalculatorTransactionUiModel(
    name = name,
    icon = icon.imageVector,
    element = this,
  )
}

enum class CategoryType(val id: Int) {
  EXPENSE(0),
  INCOME(1);

  companion object {
    fun getById(id: Int) = entries.first { it.id == id }
  }
}