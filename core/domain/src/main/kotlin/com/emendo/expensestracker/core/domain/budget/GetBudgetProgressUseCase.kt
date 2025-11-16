package com.emendo.expensestracker.core.domain.budget

import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.BudgetModel
import com.emendo.expensestracker.data.api.repository.BudgetRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import java.math.BigDecimal
import javax.inject.Inject

data class BudgetProgressData(
  val budget: BudgetModel,
  val spent: BigDecimal,
  val limit: BigDecimal,
  val percent: Float,
  val currency: CurrencyModel,
)

class GetBudgetProgressUseCase @Inject constructor(
  private val budgetRepository: BudgetRepository,
  private val transactionRepository: TransactionRepository,
  private val convertCurrencyUseCase: ConvertCurrencyUseCase,
) {
  /**
   * Returns a flow of budget progress data for all budgets, using each budget's own currency.
   */
  fun getAllBudgetsProgress(): Flow<List<BudgetProgressData>> =
    budgetRepository.getBudgets().flatMapLatest { budgetList ->
      if (budgetList.isEmpty()) {
        return@flatMapLatest flowOf(emptyList())
      }

      val flows: List<Flow<BudgetProgressData>> = budgetList.map { budget ->
        val categoryId = budget.categoryId
        val limit = budget.amount.value
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfMonth = LocalDate(now.year, now.monthNumber, 1)
        val endOfMonth = startOfMonth.plus(DatePeriod(months = 1))
        val startInstant = startOfMonth.atStartOfDayIn(TimeZone.currentSystemDefault())
        val endInstant = endOfMonth.atStartOfDayIn(TimeZone.currentSystemDefault())
        val budgetCurrency = budget.amount.currency
        transactionRepository.getTransactionsInPeriod(categoryId, startInstant, endInstant).map { transactions ->
          val spent = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { transaction ->
              convertCurrencyUseCase(
                value = transaction.amount.value,
                fromCurrencyCode = transaction.amount.currency.currencyCode,
                toCurrencyCode = budgetCurrency.currencyCode,
                conversionDate = transaction.date,
                usdToOriginalRate = transaction.usdToOriginalRate,
              )
            }
            .abs()
          val percent = if (limit > BigDecimal.ZERO) spent.toFloat() / limit.toFloat() else 0f
          BudgetProgressData(
            budget = budget,
            spent = spent,
            limit = limit,
            percent = percent.coerceIn(0f, 1f),
            currency = budgetCurrency,
          )
        }
      }
      if (flows.isEmpty()) {
        return@flatMapLatest flowOf(emptyList())
      }
      combine(flows) { it.toList() }
    }
}
