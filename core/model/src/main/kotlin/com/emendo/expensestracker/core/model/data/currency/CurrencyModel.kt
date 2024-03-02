package com.emendo.expensestracker.core.model.data.currency

import java.util.Currency

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

    fun toCurrencyModel(currencyCode: String): CurrencyModel =
      Currency.getInstance(currencyCode).toCurrencyModel()
  }
}

fun Currency.toCurrencyModel(): CurrencyModel =
  CurrencyModel(
    currencyCode = currencyCode,
    currencyName = displayName,
    currencySymbol = if (symbol == currencyCode) null else symbol,
  )