package com.emendo.expensestracker.data.api.model.transaction

import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

data class TransactionValueWithType(
  val type: TransactionType,
  val value: BigDecimal,
  val currency: CurrencyModel,
)