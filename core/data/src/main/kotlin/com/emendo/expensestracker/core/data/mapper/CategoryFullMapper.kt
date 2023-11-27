package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.data.model.category.CategoryTransactionModel
import com.emendo.expensestracker.core.data.model.category.CategoryWithTransactions
import com.emendo.expensestracker.core.data.model.category.asExternalModel
import com.emendo.expensestracker.core.database.model.CategoryFull
import com.emendo.expensestracker.core.database.model.TransactionEntity
import javax.inject.Inject

class CategoryFullMapper @Inject constructor(
  private val currencyMapper: CurrencyMapper,
) : Mapper<CategoryFull, CategoryWithTransactions> {

  override suspend fun map(from: CategoryFull): CategoryWithTransactions = with(from) {
    CategoryWithTransactions(
      categoryModel = category.asExternalModel(),
      transactions = transactions.map { toCategoryTransactionModel(it) },
    )
  }

  private suspend fun toCategoryTransactionModel(entity: TransactionEntity) =
    CategoryTransactionModel(
      id = entity.id,
      value = entity.value,
      currencyModel = currencyMapper.map(entity.currencyCode),
    )
}