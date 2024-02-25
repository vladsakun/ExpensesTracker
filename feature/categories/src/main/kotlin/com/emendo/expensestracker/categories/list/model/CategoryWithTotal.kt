package com.emendo.expensestracker.categories.list.model

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.category.CategoryWithTotalTransactions

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

// Todo refactor
fun toCategoryModel(category: CategoryWithTotal.Category): CategoryModel =
  object : CategoryModel {
    override val id: Long = category.id
    override val name = category.name
    override val icon = category.icon
    override val color = category.color
    override val ordinalIndex = category.ordinalIndex
    override val type = category.type
    override val currency = category.currency
  }

fun toCategoryWithTotal(category: CategoryWithTotalTransactions): CategoryWithTotal =
  CategoryWithTotal(
    totalAmount = category.totalAmount,
    category = category.asCategory()
  )

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
