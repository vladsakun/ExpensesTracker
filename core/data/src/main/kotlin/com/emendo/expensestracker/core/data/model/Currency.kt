package com.emendo.expensestracker.core.data.model

enum class Currency constructor(
  val id: Int,
  val currencySymbol: String,
) {
  USD(1, "$"),
  EUR(2, "€"),
  GBP(3, "£"),
  JPY(4, "¥"),
  AUD(5, "$"),
  CAD(6, "$"),
  CHF(7, "Fr"),
  CNY(8, "¥"),
  SEK(9, "kr"),
  NZD(10, "$");

  companion object {
    private val values = Currency.values().associateBy { it.id }

    fun getById(id: Int): Currency {
      return values[id] ?: throw IllegalArgumentException("No Currency with id $id")
    }
  }
}