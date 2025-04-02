package com.emendo.expensestracker.data.api.extensions

import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.data.api.amount.AmountFormatter

fun Amount.abs(amountFormatter: AmountFormatter): Amount =
  amountFormatter.format(this.value.abs(), this.currency)