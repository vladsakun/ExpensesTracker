package com.emendo.expensestracker.core.data.model

data class CategoryWithTotalTransactions(
  val categoryModel: CategoryModel,
  val transactions: List<CategoryTransactionModel>,
  val totalFormatted: String,
)