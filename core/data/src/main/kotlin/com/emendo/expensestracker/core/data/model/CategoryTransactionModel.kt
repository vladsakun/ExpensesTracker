package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.math.BigDecimal

// Todo rethink
data class CategoryTransactionModel(
  val id: Long,
  val value: BigDecimal,
  val currencyModel: CurrencyModel,
)