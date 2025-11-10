package com.emendo.expensestracker.data.api.model.transaction

import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import kotlinx.datetime.Instant
import java.math.BigDecimal

data class TransactionValueWithType(
  val type: TransactionType,
  val value: BigDecimal,
  val currency: CurrencyModel,
  val date: Instant,
  val usdToOriginalRate: BigDecimal,
)