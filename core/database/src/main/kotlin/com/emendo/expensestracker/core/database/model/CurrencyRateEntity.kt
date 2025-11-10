package com.emendo.expensestracker.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.emendo.expensestracker.core.database.util.BASE_CURRENCY_CODE_KEY
import com.emendo.expensestracker.core.database.util.RATE_DATE
import com.emendo.expensestracker.core.database.util.TABLE_CURRENCY_RATE
import com.emendo.expensestracker.core.database.util.TARGET_CURRENCY_CODE_KEY
import com.emendo.expensestracker.core.model.data.currency.CurrencyModels.CURRENCY_RATES_BASE
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.math.BigDecimal

@Entity(
  tableName = TABLE_CURRENCY_RATE,
  primaryKeys = [BASE_CURRENCY_CODE_KEY, TARGET_CURRENCY_CODE_KEY, RATE_DATE],
  indices = [Index(value = [TARGET_CURRENCY_CODE_KEY, RATE_DATE], unique = true)],
)
data class CurrencyRateEntity(
  @ColumnInfo(name = BASE_CURRENCY_CODE_KEY)
  val baseCurrencyCode: String = CURRENCY_RATES_BASE, // USD as base currency for all rates
  @ColumnInfo(name = TARGET_CURRENCY_CODE_KEY)
  val targetCurrencyCode: String, // e.g. EUR, GBP, etc.
  @ColumnInfo(name = RATE_DATE)
  val rateDate: String, // YYYY-MM-DD
  @ColumnInfo(name = "rate_multiplier")
  val rateMultiplier: BigDecimal,
  @ColumnInfo(name = "created_at")
  val createdAt: Instant = Clock.System.now(),
)