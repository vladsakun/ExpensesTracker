package com.emendo.expensestracker.core.model.data

data class CurrencyModel(
  val currencyCode: String,
  val currencyName: String,
  val currencySymbol: String? = null,
) {
  inline val currencySymbolOrCode: String
    get() = currencySymbol ?: currencyCode
}