package com.emendo.expensestracker.data.api.model.transaction

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.data.api.model.AccountModel
import kotlinx.datetime.Instant

data class TransactionModel(
  val id: Long,
  val source: AccountModel,
  val target: TransactionTarget,
  val amount: Amount,
  val type: TransactionType,
  val date: Instant,
  val note: String?,
  val transferReceivedAmount: Amount? = null,
)