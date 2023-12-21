package com.emendo.expensestracker.core.model.data

import java.math.BigDecimal

data class Amount(
  val formattedValue: String,
  val currency: CurrencyModel,
  val value: BigDecimal,
) {
  companion object {
    val ZERO: Amount = Amount(
      formattedValue = "",
      currency = CurrencyModel.EMPTY,
      value = BigDecimal.ZERO,
    )

    val Mock: Amount = Amount(
      formattedValue = "$1223.45",
      value = BigDecimal.valueOf(122345, 2),
      currency = CurrencyModel.USD,
    )
  }
}