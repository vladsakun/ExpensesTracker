package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.database.model.category.CategoryWithTransactionsFull
import com.emendo.expensestracker.core.database.model.transaction.TransactionEntity
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryTransactionModel
import com.emendo.expensestracker.data.api.model.category.CategoryWithTransactions
import javax.inject.Inject

class CategoryFullMapper @Inject constructor() : Mapper<CategoryWithTransactionsFull, CategoryWithTransactions> {

  override suspend fun map(from: CategoryWithTransactionsFull): CategoryWithTransactions = with(from) {
    CategoryWithTransactions(
      categoryModel = asExternalModel(category),
      transactions = transactions.map { toCategoryTransactionModel(it) },
    )
  }

  private fun toCategoryTransactionModel(entity: TransactionEntity) =
    CategoryTransactionModel(
      id = entity.id,
      value = entity.value,
      currencyModel = CurrencyModel.toCurrencyModel(entity.currencyCode),
    )
}