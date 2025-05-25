package com.emendo.expensestracker.data.api.utils

import kotlinx.datetime.*
import java.time.Year

object ExpeDateUtils {

  fun getFirstAndLastDayOfMonth(date: LocalDateTime): Pair<Instant, Instant> {
    val isLeapYear = Year.isLeap(date.year.toLong())
    val firstDayOfMonth = LocalDate(date.year, date.monthNumber, 1)
    val lastDayOfMonth = LocalDate(date.year, date.monthNumber, firstDayOfMonth.month.length(isLeapYear))

    // Fallback to UTC, because we don't care about exact time. We just need the start of day
    val firstInstant = firstDayOfMonth.atStartOfDayIn(TimeZone.UTC)
    // Todo think about nanoseconds
    val lastInstant = lastDayOfMonth.atTime(23, 59, 59).toInstant(TimeZone.UTC)

    return Pair(firstInstant, lastInstant)
  }

  fun getFirstAndLastDayOfCurrentMonth(): Pair<Instant, Instant> {
    val now = Clock.System.now()
    val date = now.toLocalDateTime(TimeZone.UTC)
    val isLeapYear = Year.isLeap(date.year.toLong())
    val firstDayOfMonth = LocalDate(date.year, date.monthNumber, 1)
    val lastDayOfMonth = LocalDate(date.year, date.monthNumber, firstDayOfMonth.month.length(isLeapYear))

    val firstInstant = firstDayOfMonth.atStartOfDayIn(TimeZone.UTC)
    val lastInstant = lastDayOfMonth.atTime(23, 59, 59).toInstant(TimeZone.UTC)

    return Pair(firstInstant, lastInstant)
  }
}