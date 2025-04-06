package com.emendo.expensestracker.core.database.model.account

import androidx.room.Entity
import java.math.BigDecimal

@Entity
data class AccountDetailUpdate(
  val id: Long,
  val name: String,
  val balance: BigDecimal,
  val currencyCode: String,
  val iconId: Int,
  val colorId: Int,
)

@Entity
data class AccountOrdinalIndexUpdate(
  val id: Long,
  val ordinalIndex: Int,
)