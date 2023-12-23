package com.emendo.expensestracker.core.data

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.applyDefaultDecimalStyle() = apply {
  setScale(2, RoundingMode.HALF_EVEN)
}

fun BigDecimal.divideWithScale(
  divisor: BigDecimal,
  scale: Int = 2,
  roundingMode: RoundingMode = RoundingMode.HALF_EVEN,
): BigDecimal =
  divide(divisor, scale, roundingMode)

val BigDecimal.isFloatingPointNumber: Boolean
  get() = remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0
