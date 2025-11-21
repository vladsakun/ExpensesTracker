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
  /**
   * Creates a sample account and a sample category for testing purposes.
   * Returns a Pair of the created AccountModel (or null) and the created category ID (or null).
   */
  suspend operator fun invoke() = withContext(ioDispatcher) {
    val accountName = "Карта Банку 1"
    val accountCurrency = CurrencyModel.toCurrencyModel("UAH")
    val accountIcon = IconModel.CREDITCARD
    val accountColor = ColorModel.Blue
    val accountBalance = BigDecimal(50000)

    //    val createAccountDeff = async {
    //      accountRepository.createAccount(
    //        currency = accountCurrency,
    //        name = accountName,
    //        icon = accountIcon,
    //        color = accountColor,
    //        balance = accountBalance,
    //      )
    //    }

    val restaurantCategoryName = "Освіта"
    val incomeCategoryName = "Інше"
    val restaurantCategoryIcon = IconModel.EDUCATION
    val incomeCategoryIcon = IconModel.CREDITCARD
    val restaurantCategoryColor = ColorModel.Green
    val incomeCategoryColor = ColorModel.Green

    val createCategoriesDeff: List<Deferred<Long>> = listOf(
      async {
        categoryRepository.createCategory(
          name = restaurantCategoryName,
          icon = restaurantCategoryIcon,
          color = restaurantCategoryColor,
          type = CategoryType.EXPENSE,
        )
      },
      //      async {
      //        categoryRepository.createCategory(
      //          name = incomeCategoryName,
      //          icon = incomeCategoryIcon,
      //          color = incomeCategoryColor,
      //          type = CategoryType.INCOME,
      //        )
      //      }
    )

    val accountId = 1L
    val categoryIds = createCategoriesDeff.awaitAll()

    // Get actual created account and categories
    val account = accountRepository.retrieveAccounts().find { it.id == accountId }!!
    val restaurantId = categoryIds.first()
    //    val incomeId = categoryIds[1]
    val categories = categoryRepository.getCategories().first()
    val restaurantCategory = categories.find { it.id == restaurantId }!!
    //    val incomeCategory = categories.find { it.id == incomeId }!!

    val transactionsDeff = mutableListOf<Deferred<Unit>>()

    val year = 2025
    for (month in 1..12) {
      repeat(10) {
        val date = LocalDate(year, month, it + 1).atStartOfDayIn(TimeZone.currentSystemDefault())
        val randomExpense = (500..1000).random() + it * 10
        transactionsDeff.add(async {
          transactionRepository.createTransaction(
            source = account,
            target = restaurantCategory,
            subcategoryId = null,
            amount = Amount(
              formattedValue = "$randomExpense",
              currency = account.currency,
              value = BigDecimal(randomExpense),
            ),
            note = "Книги",
            date = date,
          )
        })
      }
      //      repeat(3) {
      //        val date = LocalDate(year, month, it + 1).atStartOfDayIn(TimeZone.currentSystemDefault())
      //        val randomIncome = (1000..1500).random() + it * 100
      //        transactionsDeff.add(async {
      //          transactionRepository.createTransaction(
      //            source = account,
      //            target = incomeCategory,
      //            subcategoryId = null,
      //            amount = Amount(
      //              formattedValue = "$randomIncome",
      //              currency = account.currency,
      //              value = BigDecimal(randomIncome),
      //            ),
      //            note = "Other income",
      //            date = date,
      //          )
      //        })
      //      }
    }

    awaitAll(*transactionsDeff.toTypedArray())

    budgetRepository.createBudget(
      name = "Книги",
      iconId = restaurantCategory.icon.id,
      colorId = restaurantCategory.color.id,
      amount = BigDecimal(15000),
      period = BudgetPeriod.MONTHLY,
      categoryId = restaurantCategory.id,
      currencyCode = account.currency.currencyCode,
    )
  }
}