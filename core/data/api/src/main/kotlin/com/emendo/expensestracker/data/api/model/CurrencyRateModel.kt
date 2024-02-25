package com.emendo.expensestracker.data.api.model

import java.math.BigDecimal

data class CurrencyRateModel(
  val currencyCode: String,
  val rate: BigDecimal,
)