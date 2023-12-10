package com.emendo.expensestracker.core.data.model.transaction

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.datetime.Instant
import java.math.BigDecimal

data class TransactionModel(
  val id: Long,
  val source: AccountModel,
  val target: TransactionTargetUiModel,
  val formattedValue: String,
  val value: BigDecimal,
  val type: TransactionType,
  val date: Instant,
  val note: String?,
  val transferReceivedValue: BigDecimal? = null,
  val transferCurrencyModel: CurrencyModel? = null,
)