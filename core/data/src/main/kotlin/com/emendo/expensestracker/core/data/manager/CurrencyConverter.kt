package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.data.model.CurrencyRateModel
import java.math.BigDecimal

interface CurrencyConverter {
  suspend fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    currencyRates: List<CurrencyRateModel>,
  ): BigDecimal
}