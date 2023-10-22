package com.emendo.expensestracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.util.TABLE_CURRENCY_RATE
import java.math.BigDecimal

@Entity(tableName = TABLE_CURRENCY_RATE)
data class CurrencyRateEntity(
  @PrimaryKey
  val currencyCode: String,
  val rate: BigDecimal,
)