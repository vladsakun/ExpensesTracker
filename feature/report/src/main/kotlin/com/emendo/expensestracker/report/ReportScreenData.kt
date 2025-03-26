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
import kotlinx.serialization.Serializable

@Immutable
data class ReportScreenData(
  val balanceDate: TextValue,
  val balance: Amount,
  val pieChartData: ImmutableList<ReportPieChartSlice>?,
  val reportSum: ReportSum,
  val categoryValues: ImmutableList<CategoryValue>,
  val transactionType: TransactionType,
  val periods: ImmutableList<ReportPeriod>,
) {

  data class CategoryValue(
    val categoryId: Long,
    val icon: IconModel,
    val categoryName: TextValue,
    val amount: Amount,
    val color: ColorModel,
  )

  data class ReportSum(
    val label: TextValue,
    val value: Amount,
    val type: TransactionType,
  )
}

data class ReportPieChartSlice(
  val categoryId: Long,
  val value: Float,
  val color: ColorModel,
)

// TODO remove java.io.Serializable
@Serializable
sealed class ReportPeriod : java.io.Serializable {
  abstract val label: TextValue
  abstract val selected: Boolean

  @Serializable
  data class Date(
    override val label: TextValue,
    override val selected: Boolean = false,
    val start: Instant,
    val end: Instant,
  ) : ReportPeriod() {

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Date

      if (label != other.label) return false
      if (start != other.start) return false
      if (end != other.end) return false

      return true
    }

    override fun hashCode(): Int {
      var result = label.hashCode()
      result = 31 * result + start.hashCode()
      result = 31 * result + end.hashCode()
      return result
    }
  }

  @Serializable
  data class AllTime(
    override val label: TextValue = resourceValueOf(R.string.report_all_time),
    override val selected: Boolean = false,
  ) : ReportPeriod() {

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as AllTime

      return label == other.label
    }

    override fun hashCode(): Int {
      return label.hashCode()
    }
  }

  @Serializable
  data class Custom(
    override val label: TextValue = resourceValueOf(R.string.report_custom),
    override val selected: Boolean = false,
    val start: Instant? = null,
    val end: Instant? = null,
  ) : ReportPeriod() {

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Custom

      if (label != other.label) return false
      if (start != other.start) return false
      if (end != other.end) return false

      return true
    }

    override fun hashCode(): Int {
      var result = label.hashCode()
      result = 31 * result + (start?.hashCode() ?: 0)
      result = 31 * result + (end?.hashCode() ?: 0)
      return result
    }
  }

  fun getFromAndTo(): Pair<Instant, Instant> = when (this) {
    is Date -> start to end
    is AllTime -> Instant.DISTANT_PAST to Instant.DISTANT_FUTURE
    is Custom -> checkNotNull(start) to checkNotNull(end)
  }
}
