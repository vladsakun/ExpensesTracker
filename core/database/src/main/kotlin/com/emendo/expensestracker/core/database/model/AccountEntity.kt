package com.emendo.expensestracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.util.TABLE_ACCOUNT
import java.math.BigDecimal

@Entity(tableName = TABLE_ACCOUNT)
data class AccountEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val balance: BigDecimal,
  val currencyId: Int,
  val iconId: Int,
  val colorId: Int,
)