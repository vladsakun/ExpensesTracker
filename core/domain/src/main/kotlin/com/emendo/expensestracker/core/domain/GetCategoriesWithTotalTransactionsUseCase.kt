package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.manager.CurrencyConverter
import com.emendo.expensestracker.core.data.model.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.data.model.CategoryWithTransactions
import com.emendo.expensestracker.core.data.repository.api.CategoryRepository
import com.emendo.expensestracker.core.data.repository.api.CurrencyRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetCategoriesWithTotalTransactionsUseCase @Inject constructor(
  private val categoryRepository: CategoryRepository,
  private val currencyRepository: CurrencyRepository,
  private val currencyConverter: CurrencyConverter,
  private val amountFormatter: AmountFormatter,
) {

  operator fun invoke(): Flow<List<CategoryWithTotalTransactions>> {
    val combinedFlow: Flow<Pair<List<CategoryWithTransactions>, CurrencyModel>> =
      categoryRepository.getCategoriesWithTransactions()
        .combine(currencyRepository.generalCurrency) { transactions, currency ->
          transactions to currency
        }

    return combinedFlow.transform { (transactions, currency) ->
      val remappedTransactions: List<CategoryWithTotalTransactions> = transactions.map { categoryWithTransactions ->
        val totalSum = categoryWithTransactions.transactions.map {
          currencyConverter.convert(it.value, it.currencyModel.currencyCode, currency.currencyCode)
        }.sumOf { it }

        CategoryWithTotalTransactions(
          categoryModel = categoryWithTransactions.categoryModel,
          transactions = categoryWithTransactions.transactions,
          totalFormatted = amountFormatter.format(
            amount = totalSum,
            currency = currency
          )
        )
      }
      emit(remappedTransactions)
    }
  }
}