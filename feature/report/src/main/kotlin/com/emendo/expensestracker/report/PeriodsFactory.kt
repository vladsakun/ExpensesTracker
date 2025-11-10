package com.emendo.expensestracker.report

import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import com.emendo.expensestracker.data.api.utils.ExpeDateUtils
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.textValueOf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.*
import java.time.Year
import java.time.format.TextStyle
import javax.inject.Inject

class PeriodsFactory @Inject constructor(
  private val localeManager: ExpeLocaleManager,
  private val transactionRepository: TransactionRepository,
) {

  suspend fun getPeriods(selectedPeriod: ReportPeriod): ImmutableList<ReportPeriod> {
    val now = Clock.System.now()
    val nowLocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    val firstTransaction = transactionRepository.retrieveFirstTransaction()?.date ?: now

    val startYear = firstTransaction.toLocalDateTime(TimeZone.UTC).year
    val yearNow = nowLocalDate.year
    val diff = (yearNow - startYear).coerceAtMost(3) // Maximum 3 years history suggestion

    // Years
    val periods = mutableListOf(
      if (selectedPeriod is ReportPeriod.Custom) selectedPeriod else ReportPeriod.Custom(),
      ReportPeriod.AllTime()
    )
    for (i in yearNow downTo yearNow - diff) {
      val year = Year.of(i)
      val start = year.atDay(1).atStartOfDay().toInstant(TimeZone.UTC.toJavaZoneOffset()).toKotlinInstant()
      val end = year.atDay(year.length()).atStartOfDay().toInstant(TimeZone.UTC.toJavaZoneOffset()).toKotlinInstant()
      periods.add(
        ReportPeriod.Date(
          label = textValueOf(year.toString()),
          start = start,
          end = end,
        ),
      )
    }

    // Months
    for (i in yearNow downTo yearNow - diff) {
      val year = Year.of(i)
      val monthRange: OpenEndRange<Month> = if (year.value == nowLocalDate.year) {
        year.atMonth(nowLocalDate.month).month.rangeUntil(year.atMonth(1).month)
      } else {
        year.atMonth(12).month.rangeUntil(year.atMonth(1).month)
      }
      for (monthValue in monthRange.start.value downTo monthRange.endExclusive.value) {
        val month: java.time.Month = Month.of(monthValue)
        val date = LocalDateTime(year = year.value, month = month, 1, 0, 0)
        val (start, end) = ExpeDateUtils.getFirstAndLastDayOfMonth(date)
        periods.add(
          ReportPeriod.Date(
            label = getPeriodLabel(month, year),
            start = start,
            end = end,
          ),
        )
      }
    }

    return periods.reversed().toImmutableList()
  }

  fun getPeriodLabel(month: java.time.Month, year: Year): TextValue.Value =
    textValueOf(month.getMonthLabel() + year.getYearLabel())

  private fun Int.getYearLabel(): String = Year.of(this).getYearLabel()
  private fun Year.getYearLabel(): String = " '${toString().substring(2)}"

  private fun java.time.Month.getMonthLabel(): String? =
    getDisplayName(TextStyle.FULL_STANDALONE, localeManager.getLocale())

}