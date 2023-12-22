package com.emendo.expensestracker.core.domain.category

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.manager.CurrencyConverter
import com.emendo.expensestracker.core.data.model.CurrencyRateModel
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.data.model.category.CategoryWithTransactions
import com.emendo.expensestracker.core.data.repository.api.CurrencyRateRepository
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
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
  private val getUserCreateCategoriesWithNotEmptyPrepopulatedUseCase: GetUserCreateCategoriesWithNotEmptyPrepopulatedUseCase,
) {

  operator fun invoke(): Flow<List<CategoryWithTotalTransactions>> {
    val combinedFlow: Flow<CategoryWithTotalTransactionsAndCurrencies> =
      combine(
        getUserCreateCategoriesWithNotEmptyPrepopulatedUseCase(),
        userDataRepository.generalCurrency,
        currencyRateRepository.rates,
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