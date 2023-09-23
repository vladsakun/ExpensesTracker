package com.emendo.expensestracker.core.database.model

import androidx.room.Embedded
import java.math.BigDecimal

data class TransactionFull(
  val id: Long,
  val currencyId: Int,
  val sourceId: Int,
  val targetId: Int,
  val type: Int,
  val value: BigDecimal,
  @Embedded(prefix = "source_account_") val source: AccountEntity,
  @Embedded(prefix = "target_account_") val targetAccount: AccountEntity?,
  @Embedded(prefix = "target_category_") val targetCategory: CategoryEntity?,
)