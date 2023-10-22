package com.emendo.expensestracker.core.data.manager

import java.math.BigDecimal

interface CurrencyConverter {
  suspend fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
  ): BigDecimal
}