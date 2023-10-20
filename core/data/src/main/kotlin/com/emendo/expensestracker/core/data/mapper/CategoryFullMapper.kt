package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.model.CategoryTransactionModel
import com.emendo.expensestracker.core.data.model.CategoryWithTransactions
import com.emendo.expensestracker.core.data.model.asExternalModel
import com.emendo.expensestracker.core.data.repository.api.CurrencyRepository
import com.emendo.expensestracker.core.database.model.CategoryFull
import com.emendo.expensestracker.core.database.model.TransactionEntity
import javax.inject.Inject

class CategoryFullMapper @Inject constructor(
  private val currencyRepository: CurrencyRepository,
) : Mapper<CategoryFull, CategoryWithTransactions> {

  override suspend fun map(from: CategoryFull): CategoryWithTransactions = with(from) {
    return CategoryWithTransactions(
      categoryModel = category.asExternalModel(),
      transactions = transactions.map(::toCategoryTransactionModel),
    )
  }

  private fun toCategoryTransactionModel(entity: TransactionEntity) =
    CategoryTransactionModel(
      id = entity.id,
      value = entity.value,
      currencyModel = currencyRepository.findCurrencyModel(entity.currencyCode),
    )
}