package com.emendo.expensestracker.createtransaction.transaction.domain

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.manager.CurrencyCacheManager
import java.math.BigDecimal
import javax.inject.Inject

class GetDefaultAmountUseCase @Inject constructor(
  private val amountFormatter: AmountFormatter,
  private val currencyCacheManager: CurrencyCacheManager,
) {

  operator fun invoke(currencyModel: CurrencyModel?): Amount {
    return amountFormatter.format(
      amount = DEFAULT_AMOUNT_VALUE,
      currency = currencyModel ?: currencyCacheManager.getGeneralCurrencySnapshot(),
    )
  }

  companion object {
    val DEFAULT_AMOUNT_VALUE: BigDecimal = BigDecimal.ZERO
  }
}