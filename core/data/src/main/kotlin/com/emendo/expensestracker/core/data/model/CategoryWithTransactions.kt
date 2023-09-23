package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.database.model.CategoryFull
import com.emendo.expensestracker.core.database.model.TransactionEntity

data class CategoryWithTransactions(
  val category: Category,
  val transactions: List<TransactionEntity>,
  val totalFormatted: String,
) {
}

fun CategoryFull.asExternalModel(amountFormatter: AmountFormatter): CategoryWithTransactions {
  return CategoryWithTransactions(
    category = category.asExternalModel(),
    transactions = transactions,
    totalFormatted = amountFormatter.format(transactions.sumOf { it.value }),
  )
}