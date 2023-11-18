package com.emendo.expensestracker.core.data.model.category

data class CategoryWithTotalTransactions(
  val categoryModel: CategoryModel,
  val transactions: List<CategoryTransactionModel>,
  val totalFormatted: String,
)