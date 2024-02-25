package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.database.model.CategoryFull
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryTransactionModel
import com.emendo.expensestracker.data.api.model.category.CategoryWithTransactions
import javax.inject.Inject

class CategoryFullMapper @Inject constructor() : Mapper<CategoryFull, CategoryWithTransactions> {

  override suspend fun map(from: CategoryFull): CategoryWithTransactions = with(from) {
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