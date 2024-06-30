package com.emendo.expensestracker.report.domain

import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import java.math.BigDecimal
import javax.inject.Inject

class GetCategoriesWithTotalTransactionsUseCase @Inject constructor() {

  operator fun invoke(
    transactions: List<TransactionModel>,
    transactionType: TransactionType,
  ): List<Pair<CategoryModel, BigDecimal>> {
    val categoryExpenses: Map<CategoryModel?, List<TransactionModel>> = transactions
      .filter { it.type == transactionType }
      .groupBy { transaction ->
        val category = transaction.target as? CategoryModel ?: return@groupBy null
        category
      }
    val categoryWithTotal: List<Pair<CategoryModel, BigDecimal>> = categoryExpenses.mapNotNull { entry ->
      val category = entry.key ?: return@mapNotNull null
      val sum = entry.value.sumOf { it.amount.value }.abs()
      Pair(category, sum)
    }
    return categoryWithTotal
  }
}