package com.emendo.expensestracker.core.data.model.category

import com.emendo.expensestracker.core.model.data.CategoryWithOrdinalIndex

data class CategoryWithTotalTransactions(
  val categoryModel: CategoryModel,
  //  val transactions: List<CategoryTransactionModel>,
  val totalFormatted: String,
)

fun toCategoryWithOrdinalIndex(category: CategoryWithTotalTransactions) =
  CategoryWithOrdinalIndex(
    id = category.categoryModel.id,
    ordinalIndex = category.categoryModel.ordinalIndex,
  )