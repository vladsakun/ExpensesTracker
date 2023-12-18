package com.emendo.expensestracker.core.data.model.transaction

import java.math.BigDecimal

data class TransactionValueWithType(
  val type: TransactionType,
  val value: BigDecimal,
)