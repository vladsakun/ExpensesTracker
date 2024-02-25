package com.emendo.expensestracker.data.api.model.category

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget

interface CategoryModel : TransactionTarget {
  override val id: Long
  override val name: TextValue
  override val icon: IconModel
  override val color: ColorModel
  override val ordinalIndex: Int
  override val currency: CurrencyModel?
  val type: CategoryType
}