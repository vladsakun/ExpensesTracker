package com.emendo.expensestracker.data.api.model.category

data class CategoryWithTransactions(
  val categoryModel: CategoryModel,
  val transactions: List<CategoryTransactionModel>,
)