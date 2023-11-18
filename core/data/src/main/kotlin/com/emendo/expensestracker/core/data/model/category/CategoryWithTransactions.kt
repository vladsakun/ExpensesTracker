package com.emendo.expensestracker.core.data.model.category

data class CategoryWithTransactions(
  val categoryModel: CategoryModel,
  val transactions: List<CategoryTransactionModel>,
)