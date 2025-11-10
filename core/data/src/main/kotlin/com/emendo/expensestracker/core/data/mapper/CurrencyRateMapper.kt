package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import com.emendo.expensestracker.data.api.utils.todayAsString
import java.math.BigDecimal
import java.math.RoundingMode

internal fun toCurrencyRateModel(currencyRateEntity: CurrencyRateEntity) =
  CurrencyRateModel(
    currencyCode = currencyRateEntity.targetCurrencyCode,
    rate = currencyRateEntity.rateMultiplier,
  )

internal fun toCurrencyRateEntity(currencyRate: Map.Entry<String, Double>) =
  CurrencyRateEntity(
    targetCurrencyCode = currencyRate.key,
    rateMultiplier = BigDecimal(currencyRate.value).setScale(4, RoundingMode.HALF_EVEN),
    rateDate = todayAsString(),
  )