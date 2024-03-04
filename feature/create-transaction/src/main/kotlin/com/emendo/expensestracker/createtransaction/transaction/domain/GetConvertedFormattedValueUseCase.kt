package com.emendo.expensestracker.createtransaction.transaction.domain

import com.emendo.expensestracker.core.domain.currency.ConvertCurrencyUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import java.math.BigDecimal
import javax.inject.Inject

class GetConvertedFormattedValueUseCase @Inject constructor(
  private val amountFormatter: AmountFormatter,
  private val convertCurrencyUseCase: ConvertCurrencyUseCase,
) {

  operator fun invoke(
    value: BigDecimal,
    fromCurrency: CurrencyModel,
    toCurrency: CurrencyModel,
  ): Amount {
    val convertedValue = convertCurrencyUseCase(
      value = value,
      fromCurrencyCode = fromCurrency.currencyCode,
      toCurrencyCode = toCurrency.currencyCode,
    )
    return amountFormatter.format(convertedValue, toCurrency)
  }

}