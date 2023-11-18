package com.emendo.expensestracker.core.app.base.exception

import com.emendo.expensestracker.core.model.data.exception.Actionable
import com.emendo.expensestracker.core.model.data.exception.ActionableException

class CurrencyRateNotFoundException(
  private val fetchCurrencyRates: suspend () -> Unit,
) : ActionableException(errorMessage = "Currency rate not found") {

  override val actionable = Actionable(::fetchCurrencyRates)
}