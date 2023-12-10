package com.emendo.expensestracker.categories.list.model

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.model.data.CurrencyModel

data class CategoryWithTotal(
  val category: Category,
  val totalFormatted: String,
) {
  data class Category(
    override val id: Long,
    override val name: TextValue,
    override val icon: IconModel,
    override val color: ColorModel,
    override val ordinalIndex: Int,
    val type: CategoryType,
    override val currency: CurrencyModel? = null,
  ) : TransactionTarget {
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

fun toCategoryWithTotal(category: CategoryWithTotalTransactions) =
  CategoryWithTotal(
    totalFormatted = category.totalFormatted,
    category = category.asCategory()
  )

private fun CategoryWithTotalTransactions.asCategory() = with(categoryModel) {
  CategoryWithTotal.Category(
    id = id,
    name = name,
    icon = icon,
    color = color,
    ordinalIndex = ordinalIndex,
    type = type,
  )
}
