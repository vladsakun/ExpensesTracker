package com.emendo.expensestracker.core.data.model

import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import java.math.BigDecimal
import java.math.RoundingMode

data class CurrencyRateModelImpl(
  override val currencyCode: String,
  override val rate: BigDecimal,
) : CurrencyRateModel

fun toCurrencyRateModel(currencyRateEntity: CurrencyRateEntity) =
  currencyRateEntity.rate?.let {
    CurrencyRateModelImpl(
      currencyCode = currencyRateEntity.currencyCode,
      rate = it,
    )
  }

fun toCurrencyRateEntity(currencyRate: Map.Entry<String, Double>) = CurrencyRateEntity(
  currencyCode = currencyRate.key,
  rate = BigDecimal(currencyRate.value).setScale(4, RoundingMode.HALF_EVEN),
)