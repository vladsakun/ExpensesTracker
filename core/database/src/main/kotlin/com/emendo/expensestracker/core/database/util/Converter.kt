package com.emendo.expensestracker.core.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import java.math.BigDecimal

class Converter {
  @TypeConverter
  fun fromBigDecimal(value: BigDecimal?): String? = value?.toString()

  @TypeConverter
  fun toBigDecimal(value: String?): BigDecimal? = value?.toBigDecimal()

  @TypeConverter
  fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()

  @TypeConverter
  fun toInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)
}