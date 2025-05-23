package com.emendo.expensestracker.report

import androidx.compose.runtime.Immutable
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.resourceValueOf
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

data class ReportScreenNavigation(
  val openCategoryTransactions: StateEventWithContent<String> = consumed(),
)

@Immutable
data class ReportScreenData(
  val balanceDate: TextValue,
  val balance: Amount,
  val pieChartData: ImmutableList<ReportPieChartSlice>?,
  val reportSum: ReportSum,
  val categoryValues: ImmutableList<CategoryValue>,
  val transactionType: TransactionType,
  //  val periods: ImmutableList<ReportPeriod>,
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

  @Serializable
  data class Date(
    override val label: TextValue,
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

  fun getPeriod(): Period = when (this) {
    is Date -> Period(start, end)
    is AllTime -> Period(Instant.DISTANT_PAST, Instant.DISTANT_FUTURE)
    is Custom -> Period(checkNotNull(start), checkNotNull(end))
  }
}

@Serializable
data class Period(
  val from: Long,
  val to: Long,
) {

  constructor(from: Instant, to: Instant) : this(from.toEpochMilliseconds(), to.toEpochMilliseconds())

  fun getFromInstant(): Instant = Instant.fromEpochMilliseconds(from)
  fun getToInstant(): Instant = Instant.fromEpochMilliseconds(to)
}
