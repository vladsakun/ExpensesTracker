package com.emendo.expensestracker.report.subcategories

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import kotlinx.collections.immutable.ImmutableList

data class ReportSubcategoriesScreenData(
  val categoryName: TextValue,
  val reportSumLabel: TextValue,
  val reportSum: Amount,
  val transactionType: TransactionType,
  val color: ColorModel,
  val subcategories: ImmutableList<SubcategoryUiModel>,
) {
  data class SubcategoryUiModel(
    val id: Long,
    val name: String,
    val icon: IconModel,
    val sum: Amount,
  )
}