package com.emendo.expensestracker.data.api.model.category

import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import java.math.BigDecimal

// Todo rethink
data class CategoryTransactionModel(
  val id: Long,
  val value: BigDecimal,
  val currencyModel: CurrencyModel,
)