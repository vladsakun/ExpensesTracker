package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.database.model.CategoryFull
import com.emendo.expensestracker.core.database.model.TransactionEntity

data class CategoryWithTransactions(
  val categoryModel: CategoryModel,
  val transactions: List<TransactionEntity>,
  val totalFormatted: String,
)

fun CategoryFull.asExternalModel(amountFormatter: AmountFormatter): CategoryWithTransactions {
  return CategoryWithTransactions(
    categoryModel = category.asExternalModel(),
    transactions = transactions,
    totalFormatted = amountFormatter.format(
      amount = transactions.sumOf { it.value },
      currencyModel = CurrencyModel.getById(category.currencyId),
    ),
  )
}