package com.emendo.expensestracker.data.api.model.transaction

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.data.api.model.AccountModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.math.BigDecimal

data class TransactionModel(
  val id: Long,
  val source: AccountModel,
  val target: TransactionTarget,
  val targetSubcategory: TransactionTarget?,
  val amount: Amount,
  val type: TransactionType,
  val date: Instant,
  val timeZone: TimeZone,
  val note: String?,
  val usdToOriginalRate: BigDecimal,
  val transferReceivedAmount: Amount? = null,
)