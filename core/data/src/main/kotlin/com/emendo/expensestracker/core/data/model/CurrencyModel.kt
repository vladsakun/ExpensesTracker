package com.emendo.expensestracker.core.data.model

enum class CurrencyModel constructor(
  val id: Int,
  val currencySymbol: String,
  val currencyName: String,
) {
  USD(1, "$", "USD"),
  EUR(2, "€", "EUR"),
  GBP(3, "£", "GBP"),
  JPY(4, "¥", "JPY"),
  AUD(5, "$", "AUD"),
  CAD(6, "$", "CAD"),
  CHF(7, "Fr", "CHF"),
  CNY(8, "¥", "CNY"),
  SEK(9, "kr", "SEK"),
  NZD(10, "$", "NZD");

  companion object {
    private val values = CurrencyModel.values().associateBy { it.id }

    fun getById(id: Int): CurrencyModel {
      return values[id] ?: throw IllegalArgumentException("No Currency with id $id")
    }
  }
}