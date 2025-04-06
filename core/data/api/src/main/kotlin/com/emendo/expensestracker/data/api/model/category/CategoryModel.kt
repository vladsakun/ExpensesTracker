package com.emendo.expensestracker.data.api.model.category

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

data class CategoryModel(
  override val id: Long,
  override val name: TextValue,
  override val icon: IconModel,
  override val color: ColorModel,
  override val ordinalIndex: Int,
  override val currency: CurrencyModel? = null,
  val type: CategoryType,
  val subcategories: List<CategoryModel>,
) : TransactionTarget