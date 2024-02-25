package com.emendo.expensestracker.data.api.model

import java.math.BigDecimal

interface CurrencyRateModel {
  val currencyCode: String
  val rate: BigDecimal
}