package com.emendo.expensestracker.core.model.data

import java.util.Currency
import java.util.Locale

object CurrencyModels {
  val currencies by lazy {
    mutableSetOf(
      CurrencyModel(
        currencyCode = "BTC",
        currencyName = "Bitcoin",
        currencySymbol = "₿",
      ),
      CurrencyModel(
        currencyCode = "ETH",
        currencyName = "Ethereum",
        currencySymbol = "Ξ",
      ),
      CurrencyModel(
        currencyCode = "LTC",
        currencyName = "Litecoin",
        currencySymbol = "Ł",
      ),
      CurrencyModel(
        currencyCode = "XRP",
        currencyName = "Ripple",
        currencySymbol = "XRP",
      ),
    )
  }

  val localCurrencyCode: String
    get() = Currency.getInstance(Locale.getDefault()).currencyCode
}