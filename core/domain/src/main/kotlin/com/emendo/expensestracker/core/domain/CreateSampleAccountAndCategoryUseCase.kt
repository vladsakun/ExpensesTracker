package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.BudgetPeriod
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.BudgetRepository
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.textValueOrBlank
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import java.math.BigDecimal
import javax.inject.Inject

class CreateSampleAccountAndCategoryUseCase @Inject constructor(
  private val accountRepository: AccountRepository,
  private val categoryRepository: CategoryRepository,
  private val transactionRepository: TransactionRepository,
  private val budgetRepository: BudgetRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {

  private suspend fun createSampleAccount() {
    accountRepository.createAccount(
      name = "Card",
      balance = BigDecimal(100000),
      icon = IconModel.CREDITCARD,
      color = ColorModel.Blue,
      currency = CurrencyModel.toCurrencyModel("UAH"),
    )
  }

  suspend operator fun invoke() = withContext(ioDispatcher) {
    createSampleAccount()

    // Category definitions with valid icons
    val categoriesData = listOf(
      Triple("Housing", IconModel.HOUSE, ColorModel.Blue),
      Triple("Utilities", IconModel.ENERGY, ColorModel.Orange),
      Triple("Groceries", IconModel.LOCAL_GROCERY_STORE, ColorModel.Green),
      Triple("Transport", IconModel.LOCAL_GAS_STATION, ColorModel.Red),
      Triple("Education", IconModel.EDUCATION, ColorModel.Purple),
      Triple("Healthcare", IconModel.LOCAL_HOSPITAL, ColorModel.Red),
      Triple("Restaurants", IconModel.RESTAURANT, ColorModel.Orange),
      Triple("Cinema", IconModel.ENTERTAINMENT, ColorModel.Purple),
      Triple("Leisure", IconModel.SPA, ColorModel.Green),
      Triple("Gifts", IconModel.ENTERTAINMENT, ColorModel.Red),
      Triple("Repairs", IconModel.BUSINESS, ColorModel.Orange),
      Triple("Miscellaneous", IconModel.WALLET, ColorModel.Gray),
    )

    // Create categories
    val createCategoriesDeferred = categoriesData.map { (name, icon, color) ->
      async {
        categoryRepository.createCategory(
          name = name,
          icon = icon,
          color = color,
          type = CategoryType.EXPENSE,
        )
      }
    }

    createCategoriesDeferred.awaitAll()

    // Get actual created account and categories
    val account = accountRepository.retrieveAccounts().find { it.id == 1L }!!
    val categories = categoryRepository.getCategories().first()

    // Category clusters for budgets
    val mandatoryCategoryNames = setOf("Housing", "Utilities", "Groceries", "Transport")
    val flexibleCategoryNames = setOf("Education", "Healthcare")
    val entertainmentCategoryNames = setOf("Restaurants", "Cinema", "Leisure", "Gifts")
    val unexpectedCategoryNames = setOf("Repairs", "Miscellaneous")

    @Suppress("UNCHECKED_CAST")
    val mandatoryCategories = categories.filter { it.name.textValueOrBlank() in mandatoryCategoryNames }

    @Suppress("UNCHECKED_CAST")
    val flexibleCategories = categories.filter { it.name.textValueOrBlank() in flexibleCategoryNames }

    @Suppress("UNCHECKED_CAST")
    val entertainmentCategories = categories.filter { it.name.textValueOrBlank() in entertainmentCategoryNames }

    @Suppress("UNCHECKED_CAST")
    val unexpectedCategories = categories.filter { it.name.textValueOrBlank() in unexpectedCategoryNames }

    // Create transactions for sample data
    val transactionsDeferred = mutableListOf<Deferred<Unit>>()

    val year = 2025
    for (month in 1..12) {
      // Mandatory expenses
      mandatoryCategories.forEach { category ->
        repeat(5) { day ->
          val date = LocalDate(year, month, day + 1).atStartOfDayIn(TimeZone.currentSystemDefault())
          val randomExpense = (1000..3000).random()
          transactionsDeferred.add(async {
            transactionRepository.createTransaction(
              source = account,
              target = category,
              subcategoryId = null,
              amount = Amount(
                formattedValue = "$randomExpense",
                currency = account.currency,
                value = BigDecimal(randomExpense),
              ),
              note = null,
              date = date,
            )
          })
        }
      }

      // Flexible expenses
      flexibleCategories.forEach { category ->
        repeat(3) { day ->
          val date = LocalDate(year, month, day + 6).atStartOfDayIn(TimeZone.currentSystemDefault())
          val randomExpense = (500..1500).random()
          transactionsDeferred.add(async {
            transactionRepository.createTransaction(
              source = account,
              target = category,
              subcategoryId = null,
              amount = Amount(
                formattedValue = "$randomExpense",
                currency = account.currency,
                value = BigDecimal(randomExpense),
              ),
              note = null,
              date = date,
            )
          })
        }
      }

      // Entertainment expenses
      entertainmentCategories.forEach { category ->
        repeat(2) { day ->
          val date = LocalDate(year, month, day + 10).atStartOfDayIn(TimeZone.currentSystemDefault())
          val randomExpense = (300..1000).random()
          transactionsDeferred.add(async {
            transactionRepository.createTransaction(
              source = account,
              target = category,
              subcategoryId = null,
              amount = Amount(
                formattedValue = "$randomExpense",
                currency = account.currency,
                value = BigDecimal(randomExpense),
              ),
              note = null,
              date = date,
            )
          })
        }
      }

      // Unexpected expenses
      unexpectedCategories.forEach { category ->
        repeat(1) { day ->
          val date = LocalDate(year, month, day + 15).atStartOfDayIn(TimeZone.currentSystemDefault())
          val randomExpense = (500..2000).random()
          transactionsDeferred.add(async {
            transactionRepository.createTransaction(
              source = account,
              target = category,
              subcategoryId = null,
              amount = Amount(
                formattedValue = "$randomExpense",
                currency = account.currency,
                value = BigDecimal(randomExpense),
              ),
              note = null,
              date = date,
            )
          })
        }
      }
    }

    transactionsDeferred.awaitAll()

    // Create 4 budgets
    // 1. Essential expenses - 30000 UAH / month
    budgetRepository.createBudget(
      name = "Essential expenses",
      iconId = mandatoryCategories.firstOrNull()?.icon?.id ?: IconModel.WALLET.id,
      colorId = mandatoryCategories.firstOrNull()?.color?.id ?: ColorModel.Red.id,
      amount = BigDecimal(30000),
      period = BudgetPeriod.MONTHLY,
      categoryIds = mandatoryCategories.map { it.id },
      currencyCode = account.currency.currencyCode,
    )

    // 2. Flexible expenses - 10000 UAH / month
    budgetRepository.createBudget(
      name = "Flexible expenses",
      iconId = flexibleCategories.firstOrNull()?.icon?.id ?: IconModel.EDUCATION.id,
      colorId = flexibleCategories.firstOrNull()?.color?.id ?: ColorModel.Purple.id,
      amount = BigDecimal(10000),
      period = BudgetPeriod.MONTHLY,
      categoryIds = flexibleCategories.map { it.id },
      currencyCode = account.currency.currencyCode,
    )

    // 3. Entertainment & hobbies - 5000 UAH / month
    budgetRepository.createBudget(
      name = "Entertainment & hobbies",
      iconId = entertainmentCategories.firstOrNull()?.icon?.id ?: IconModel.ENTERTAINMENT.id,
      colorId = entertainmentCategories.firstOrNull()?.color?.id ?: ColorModel.Green.id,
      amount = BigDecimal(5000),
      period = BudgetPeriod.MONTHLY,
      categoryIds = entertainmentCategories.map { it.id },
      currencyCode = account.currency.currencyCode,
    )

    // 4. Unexpected expenses - 5000 UAH / month
    budgetRepository.createBudget(
      name = "Unexpected expenses",
      iconId = unexpectedCategories.firstOrNull()?.icon?.id ?: IconModel.BUSINESS.id,
      colorId = unexpectedCategories.firstOrNull()?.color?.id ?: ColorModel.Orange.id,
      amount = BigDecimal(5000),
      period = BudgetPeriod.MONTHLY,
      categoryIds = unexpectedCategories.map { it.id },
      currencyCode = account.currency.currencyCode,
    )
  }
}
