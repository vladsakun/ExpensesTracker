package com.emendo.expensestracker.core.domain

import android.content.Context
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.core.model.data.currency.CurrencyModels
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.data.api.repository.CategoryRepository
import com.emendo.expensestracker.model.ui.ColorModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject
import com.emendo.expensestracker.app.resources.R as AppR

class CreateSampleAccountAndCategoryUseCase @Inject constructor(
  @ApplicationContext private val context: Context,
  private val accountRepository: AccountRepository,
  private val categoryRepository: CategoryRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {

  private suspend fun createSampleAccount() {
    accountRepository.createAccount(
      name = "Card",
      balance = BigDecimal(100000),
      icon = IconModel.CREDITCARD,
      color = ColorModel.Blue,
      currency = CurrencyModel.toCurrencyModel(CurrencyModels.localCurrency.currencyCode),
    )
  }

  suspend operator fun invoke() = withContext(ioDispatcher) {
    createSampleAccount()

    // Category definitions with valid icons
    val categoriesData = listOf(
      CategoryData(
        context.getString(AppR.string.category_sample_housing),
        IconModel.HOUSE,
        ColorModel.Blue,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_utilities),
        IconModel.ENERGY,
        ColorModel.Orange,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_groceries),
        IconModel.LOCAL_GROCERY_STORE,
        ColorModel.Green,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_transport),
        IconModel.LOCAL_GAS_STATION,
        ColorModel.Red,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_education),
        IconModel.EDUCATION,
        ColorModel.Purple,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_healthcare),
        IconModel.LOCAL_HOSPITAL,
        ColorModel.Red,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_restaurants),
        IconModel.RESTAURANT,
        ColorModel.Orange,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_cinema),
        IconModel.ENTERTAINMENT,
        ColorModel.Purple,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_leisure),
        IconModel.SPA,
        ColorModel.Green,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_gifts),
        IconModel.ENTERTAINMENT,
        ColorModel.Red,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_repairs),
        IconModel.BUSINESS,
        ColorModel.Orange,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_miscellaneous),
        IconModel.WALLET,
        ColorModel.Gray,
        CategoryType.EXPENSE
      ),
      CategoryData(
        context.getString(AppR.string.category_sample_salary),
        IconModel.ATTACH_MONEY,
        ColorModel.Green,
        CategoryType.INCOME
      ),
    )

    // Create categories
    val createCategoriesDeferred = categoriesData.map { (name, icon, color, type) ->
      async {
        categoryRepository.createCategory(
          name = name,
          icon = icon,
          color = color,
          type = type,
        )
      }
    }

    createCategoriesDeferred.awaitAll()
  }

  private data class CategoryData(
    val name: String,
    val icon: IconModel,
    val color: ColorModel,
    val type: CategoryType,
  )
}
