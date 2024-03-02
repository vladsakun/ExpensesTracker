package com.emendo.expensestracker.categories.list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.list.model.CategoryWithTotal
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.textValueOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import java.math.BigDecimal
import kotlin.math.pow

class CategoriesListPreviewData : PreviewParameterProvider<CategoriesListUiState> {
  val data = CategoriesListUiState.DisplayCategoriesList(
    categories = persistentMapOf(
      0 to CategoriesList(
        List(8) { index ->
          CategoryWithTotal(
            category = CategoryWithTotal.Category(
              id = index.toLong(),
              name = textValueOf("Childcare"),
              icon = IconModel.random,
              color = ColorModel.random,
              type = CategoryType.EXPENSE,
              ordinalIndex = index,
            ),
            totalAmount = Amount(
              formattedValue = "$187${10.0.pow(index)}.20",
              currency = CurrencyModel.USD,
              value = BigDecimal.valueOf(187),
            ),
          )
        }.toImmutableList(),
      )
    ),
    tabs = persistentListOf(
      TabData(R.string.expense),
      TabData(R.string.income),
    )
  )
  override val values: Sequence<CategoriesListUiState> = sequenceOf(data)
}