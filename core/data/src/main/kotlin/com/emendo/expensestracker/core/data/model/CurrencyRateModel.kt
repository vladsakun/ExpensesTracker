package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import java.math.BigDecimal
import java.math.RoundingMode

data class CurrencyRateModel(
  val currencyCode: String,
  val rate: BigDecimal,
)

fun toCurrencyRateModel(currencyRateEntity: CurrencyRateEntity) =
  currencyRateEntity.rate?.let {
    CurrencyRateModel(
      currencyCode = currencyRateEntity.currencyCode,
      rate = it,
    )
  }

fun toCurrencyRateEntity(currencyRate: Map.Entry<String, Double>) = CurrencyRateEntity(
  currencyCode = currencyRate.key,
  rate = BigDecimal(currencyRate.value).setScale(4, RoundingMode.HALF_EVEN),
)