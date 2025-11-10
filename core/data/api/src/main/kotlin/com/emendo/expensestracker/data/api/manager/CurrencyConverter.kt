package com.emendo.expensestracker.data.api.manager

import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import java.math.BigDecimal

interface CurrencyConverter {
  fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    usdToOriginalRate: BigDecimal,
    currencyRates: Map<String, CurrencyRateModel>,
  ): BigDecimal

  fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    currencyRates: Map<String, CurrencyRateModel>,
  ): BigDecimal
}