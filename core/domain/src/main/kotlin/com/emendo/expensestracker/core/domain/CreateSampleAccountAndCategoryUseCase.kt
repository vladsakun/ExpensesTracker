package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.model.ui.ColorModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class CreateSampleAccountAndCategoryUseCase @Inject constructor(
  private val accountRepository: AccountRepository,
  private val categoryRepository: CategoryRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {
  /**
   * Creates a sample account and a sample category for testing purposes.
   * Returns a Pair of the created AccountModel (or null) and the created category ID (or null).
   */
  suspend operator fun invoke() = withContext(ioDispatcher) {
    // Use provided companion/sample values from the models
    val sampleCurrency = CurrencyModel.toCurrencyModel("USD")
    val sampleAccountName = "Card"
    val sampleIcon = IconModel.CREDITCARD
    val sampleColor = ColorModel.Green
    val sampleBalance = BigDecimal(100000)

    val createAccountDeff = async {
      accountRepository.createAccount(
        currency = sampleCurrency,
        name = sampleAccountName,
        icon = sampleIcon,
        color = sampleColor,
        balance = sampleBalance,
      )
    }

    val sampleCategoryName = "Groceries"
    val sampleCategoryIcon = IconModel.GROCERIES
    val sampleCategoryColor = ColorModel.Lime
    val sampleCategoryType = CategoryType.EXPENSE

    val createCategoryDeff = async {
      categoryRepository.createCategory(
        name = sampleCategoryName,
        icon = sampleCategoryIcon,
        color = sampleCategoryColor,
        type = sampleCategoryType,
      )
    }

    awaitAll(createAccountDeff, createCategoryDeff)
  }
}