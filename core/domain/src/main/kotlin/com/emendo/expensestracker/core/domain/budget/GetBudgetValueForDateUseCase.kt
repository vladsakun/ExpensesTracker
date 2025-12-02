package com.emendo.expensestracker.core.domain.budget

import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.model.BudgetModel
import com.emendo.expensestracker.data.api.repository.BudgetRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject

class GetBudgetValueForDateUseCase @Inject constructor(
  private val budgetRepository: BudgetRepository,
  private val transactionRepository: TransactionRepository,
  private val convertCurrencyUseCase: ConvertCurrencyUseCase,
) {
  operator fun invoke(budgetId: Long, period: YearMonth): Flow<BudgetValueResult> {
    val budget = budgetRepository.getByIdSnapshot(budgetId) ?: return emptyFlow()

    val categoryIds = budget.categoryIds
    if (categoryIds.isEmpty()) return emptyFlow()

    val limit = budget.amount.value
    val budgetCurrency = budget.currency
    // Calculate start and end of month in kotlinx.datetime
    val startOfMonth = LocalDate(period.year, period.monthValue, 1)
    val endOfMonth = startOfMonth.plus(DatePeriod(months = 1))
    val startInstant = startOfMonth.atStartOfDayIn(TimeZone.currentSystemDefault())
    val endInstant = endOfMonth.atStartOfDayIn(TimeZone.currentSystemDefault())
    // Use getTransactionsByCategoriesInPeriod (returns Flow for multiple categories)
    return transactionRepository.getTransactionsByCategoriesInPeriod(
      categoryIds = categoryIds,
      from = startInstant,
      to = endInstant
    ).map { transactions ->
      var spent = BigDecimal.ZERO
      for (tx in transactions) {
        if (tx.type == TransactionType.EXPENSE) {
          spent += convertCurrencyUseCase(
            value = tx.value,
            fromCurrencyCode = tx.currency.currencyCode,
            toCurrencyCode = budgetCurrency.currencyCode,
            usdToOriginalRate = tx.usdToOriginalRate,
            conversionDate = tx.date
          )
        }
      }
      BudgetValueResult(
        spent = spent.abs(),
        limit = limit,
        budget = budget,
      )
    }
  }
}

data class BudgetValueResult(
  val spent: BigDecimal,
  val limit: BigDecimal,
  val budget: BudgetModel,
)
