package com.emendo.expensestracker.report.subcategories

import com.aay.compose.barChart.model.BarParameters
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import com.himanshoe.charty.bar.model.BarData
import kotlinx.collections.immutable.ImmutableList

data class ReportSubcategoriesScreenData(
  val categoryName: TextValue,
  val reportSumLabel: TextValue,
  val reportSum: Amount,
  val transactionType: TransactionType,
  val color: ColorModel,
  val subcategories: ImmutableList<SubcategoryUiModel>,
  val barData: ImmutableList<BarData>,
  val barData2: ImmutableList<BarParameters>,
) {
  data class SubcategoryUiModel(
    val id: Long,
    val name: String,
    val icon: IconModel,
    val sum: Amount,
  )
}