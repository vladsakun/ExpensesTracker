package com.emendo.expensestracker.core.data

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.applyDefaultDecimalStyle() = apply {
  setScale(2, RoundingMode.HALF_EVEN)
}

inline val BigDecimal.isFloatingPointNumber: Boolean
  get() = remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0
