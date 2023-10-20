package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

data class TransactionModel(
  val id: Long,
  val source: AccountModel,
  val target: TransactionTargetUiModel,
  val formattedValue: String,
  val value: BigDecimal,
  val currencyModel: CurrencyModel,
  val type: TransactionType,
  val transferReceivedValue: BigDecimal? = null,
  val transferCurrencyModel: CurrencyModel? = null,
)