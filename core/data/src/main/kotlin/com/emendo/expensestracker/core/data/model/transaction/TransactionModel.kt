package com.emendo.expensestracker.core.data.model.transaction

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.model.data.Amount
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