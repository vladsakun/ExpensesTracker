package com.emendo.expensestracker.data.api.model.category

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CategoryWithOrdinalIndex

data class CategoryWithTotalTransactions(
  val categoryModel: CategoryModel,
  val totalAmount: Amount,
  //  val transactions: List<CategoryTransactionModel>,
)

fun toCategoryWithOrdinalIndex(category: CategoryWithTotalTransactions) =
  CategoryWithOrdinalIndex(
    id = category.categoryModel.id,
    ordinalIndex = category.categoryModel.ordinalIndex,
  )