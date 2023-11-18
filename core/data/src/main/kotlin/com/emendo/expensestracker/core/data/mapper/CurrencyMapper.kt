package com.emendo.expensestracker.core.data.mapper

import com.emendo.expensestracker.core.data.mapper.base.Mapper
import com.emendo.expensestracker.core.model.data.CurrencyModel
import java.util.Currency
import javax.inject.Inject

class CurrencyMapper @Inject constructor() : Mapper<String, CurrencyModel> {
  override suspend fun map(from: String): CurrencyModel =
    toCurrencyModelBlocking(Currency.getInstance(from))

  fun toCurrencyModelBlocking(currencyCode: String) =
    toCurrencyModelBlocking(Currency.getInstance(currencyCode))

  fun toCurrencyModelBlocking(currency: Currency) =
    CurrencyModel(
      currencyCode = currency.currencyCode,
      currencyName = currency.displayName,
      currencySymbol = if (currency.symbol == currency.currencyCode) null else currency.symbol,
    )
}