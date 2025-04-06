package com.emendo.expensestracker.categories.list.model

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

data class CategoryWithTotal(
  val category: Category,
  val totalAmount: Amount,
) {
  data class Category(
    val id: Long,
    val name: TextValue,
    val icon: IconModel,
    val color: ColorModel,
    val ordinalIndex: Int,
    val type: CategoryType,
    val currency: CurrencyModel? = null,
  ) {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Category

      if (id != other.id) return false
      if (name != other.name) return false
      if (icon != other.icon) return false
      if (color != other.color) return false
      return type == other.type
    }

    override fun hashCode(): Int {
      var result = id.hashCode()
      result = 31 * result + name.hashCode()
      result = 31 * result + icon.hashCode()
      result = 31 * result + color.hashCode()
      result = 31 * result + type.hashCode()
      return result
    }
  }
}

private fun CategoryWithTotalTransactions.asCategory(): CategoryWithTotal.Category = with(categoryModel) {
  CategoryWithTotal.Category(
    id = id,
    name = name,
    icon = icon,
    color = color,
    ordinalIndex = ordinalIndex,
    type = type,
  )
}
