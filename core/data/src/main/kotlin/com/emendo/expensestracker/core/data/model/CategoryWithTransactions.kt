package com.emendo.expensestracker.core.data.model

data class CategoryWithTransactions(
  val categoryModel: CategoryModel,
  val transactions: List<CategoryTransactionModel>,
)