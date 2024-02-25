package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import java.math.BigDecimal
import java.math.RoundingMode

internal fun toCurrencyRateModel(currencyRateEntity: CurrencyRateEntity) =
  currencyRateEntity.rate?.let {
    CurrencyRateModel(
      currencyCode = currencyRateEntity.currencyCode,
      rate = it,
    )
  }

internal fun toCurrencyRateEntity(currencyRate: Map.Entry<String, Double>) = CurrencyRateEntity(
  currencyCode = currencyRate.key,
  rate = BigDecimal(currencyRate.value).setScale(4, RoundingMode.HALF_EVEN),
)