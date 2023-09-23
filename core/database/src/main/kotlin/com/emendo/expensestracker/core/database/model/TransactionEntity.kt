package com.emendo.expensestracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.util.TABLE_TRANSACTION
import java.math.BigDecimal

@Entity(tableName = TABLE_TRANSACTION)
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val sourceId: Long,
  val targetId: Long,
  val value: BigDecimal,
  val currencyId: Int,
  val type: Int,
)