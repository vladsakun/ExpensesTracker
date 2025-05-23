package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.manager.CurrencyConverter
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import com.emendo.expensestracker.data.api.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.data.api.model.category.CategoryWithTransactions
import com.emendo.expensestracker.data.api.repository.CurrencyRateRepository
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

data class CategoryWithTotalTransactionsAndCurrencies(
  val categoryWithTransactions: List<CategoryWithTransactions>,
  val generalCurrency: CurrencyModel,
  val currencyRates: Map<String, CurrencyRateModel>,
)

class GetCategoriesWithTotalTransactionsUseCase @Inject constructor(
  private val currencyRateRepository: CurrencyRateRepository,
  private val currencyConverter: CurrencyConverter,
  private val amountFormatter: AmountFormatter,
  private val userDataRepository: UserDataRepository,
  private val getUserCreatedCategoriesWithTransactionsWithNotEmptyPrepopulatedUseCase: GetUserCreatedCategoriesWithTransactionsWithNotEmptyPrepopulatedUseCase,
) {

  operator fun invoke(): Flow<List<CategoryWithTotalTransactions>> {
    val combinedFlow: Flow<CategoryWithTotalTransactionsAndCurrencies> =
      combine(
        getUserCreatedCategoriesWithTransactionsWithNotEmptyPrepopulatedUseCase(),
        userDataRepository.generalCurrency,
        currencyRateRepository.getRates(),
      ) { transactions, currency, rates ->
        CategoryWithTotalTransactionsAndCurrencies(
          categoryWithTransactions = transactions,
          generalCurrency = currency,
          currencyRates = rates,
        )
      }

    return combinedFlow.transform { (transactions, generalCurrency, rates) ->
      if (rates.isEmpty()) {
        return@transform
      }

      val remappedTransactions: List<CategoryWithTotalTransactions> = transactions.map { categoryWithTransactions ->
        val totalSum = categoryWithTransactions.transactions.map {
          currencyConverter.convert(
            value = it.value,
            fromCurrencyCode = it.currencyModel.currencyCode,
            toCurrencyCode = generalCurrency.currencyCode,
            currencyRates = rates,
          )
        }.sumOf { it }

        CategoryWithTotalTransactions(
          categoryModel = categoryWithTransactions.categoryModel,
          totalAmount = amountFormatter.format(
            amount = totalSum,
            currency = generalCurrency,
          )
        )
      }
      emit(remappedTransactions)
    }
  }
}