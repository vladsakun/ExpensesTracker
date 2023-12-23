package com.emendo.expensestracker.core.data

import com.emendo.expensestracker.core.model.data.Amount

internal fun Amount.formatPositive(): Amount =
  copy(formattedValue = if (value.isPositive) "+$formattedValue" else formattedValue)