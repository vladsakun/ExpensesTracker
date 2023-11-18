package com.emendo.expensestracker.core.model.data

import java.util.Currency
import java.util.Locale

object CurrencyModels {
  val localCurrencyCode: String
    get() = Currency.getInstance(Locale.getDefault()).currencyCode

  const val CURRENCY_RATES_BASE = "USD"
}