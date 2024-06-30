package com.emendo.expensestracker.report

import androidx.compose.runtime.Immutable
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.resourceValueOf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant

@Immutable
data class ReportScreenData(
  val balanceDate: String,
  val balance: Amount,
  val pieChartData: ImmutableList<ReportPieChartSlice>,
  val allExpenses: Amount,
  val categoryValues: ImmutableList<CategoryValue>,
  val transactionType: TransactionType,
  val reportSumLabel: TextValue,
  val periods: ImmutableList<ReportPeriod>,
  val selectedPeriod: ReportPeriod,
) {
  data class CategoryValue(
    val categoryId: Long,
    val icon: IconModel,
    val categoryName: TextValue,
    val amount: Amount,
    val color: ColorModel,
  )
}

data class ReportPieChartSlice(
  val value: Float,
  val color: ColorModel,
)

sealed class ReportPeriod(
  open val label: TextValue,
  open val selected: Boolean,
) {
  data class Date(
    override val label: TextValue,
    override val selected: Boolean = false,
    val start: Instant,
    val end: Instant,
  ) : ReportPeriod(label, selected)

  data class AllTime(
    override val label: TextValue = resourceValueOf(R.string.report_all_time),
    override val selected: Boolean = false,
  ) : ReportPeriod(label, selected)

  data class Custom(
    override val label: TextValue = resourceValueOf(R.string.report_custom),
    override val selected: Boolean = false,
    val start: Instant? = null,
    val end: Instant? = null,
  ) : ReportPeriod(label, selected)
}