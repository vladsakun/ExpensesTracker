package com.emendo.expensestracker.core.model.data.currency

import java.util.Currency
import java.util.Locale

object CurrencyModels {
  val localCurrency: Currency
    get() = Currency.getInstance(Locale.getDefault())

  const val CURRENCY_RATES_BASE = "USD"
}