package com.emendo.expensestracker.core.model.data

data class CurrencyModel(
  val currencyCode: String,
  val currencyName: String,
  val currencySymbol: String? = null,
) {
  val currencySymbolOrCode: String
    get() = currencySymbol ?: currencyCode

  companion object {
    val EMPTY = CurrencyModel(
      currencyCode = "",
      currencyName = "",
      currencySymbol = null,
    )

    val USD = CurrencyModel(
      currencyCode = "USD",
      currencyName = "US dollar",
      currencySymbol = "$",
    )
  }
}