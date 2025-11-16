package com.emendo.expensestracker.budget.create

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.model.ui.ColorModel

data class CreateBudgetScreenData(
  val name: String,
  val limit: Amount,
  val icon: IconModel,
  val color: ColorModel,
  val category: CategoryModel?,
  val currency: CurrencyModel,
  val confirmButtonEnabled: Boolean,
) {
  companion object {
    fun getDefault(
      color: ColorModel,
      iconId: Int?,
      name: String?,
      limit: Amount?,
      categoryId: CategoryModel?,
      currency: CurrencyModel = CurrencyModel.USD,
    ) = CreateBudgetScreenData(
      name = name ?: "",
      limit = limit ?: Amount.ZERO,
      icon = iconId?.let(IconModel::getById) ?: IconModel.random,
      color = color,
      category = categoryId,
      currency = currency,
      confirmButtonEnabled = !name.isNullOrBlank() && !limit?.formattedValue.isNullOrBlank(),
    )
  }
}