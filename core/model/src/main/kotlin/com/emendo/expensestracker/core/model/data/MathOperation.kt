package com.emendo.expensestracker.core.model.data

import java.math.BigDecimal

sealed interface MathOperation {
  val symbol: String
  val symbolWithWhiteSpaces: String
    get() = " $symbol "

  fun doUnformattedMath(first: BigDecimal, second: BigDecimal): BigDecimal
  fun doMath(first: BigDecimal, second: BigDecimal): BigDecimal =
    doUnformattedMath(first, second).setScale(BIG_DECIMAL_SCALE)

  data class Add(override val symbol: String = "+") : MathOperation {
    override fun doUnformattedMath(first: BigDecimal, second: BigDecimal) = first + second
  }

  data class Substract(override val symbol: String = "-") : MathOperation {
    override fun doUnformattedMath(first: BigDecimal, second: BigDecimal) = first - second
  }

  data class Multiply(override val symbol: String = "×") : MathOperation {
    override fun doUnformattedMath(first: BigDecimal, second: BigDecimal) = first * second
  }

  data class Divide(override val symbol: String = "÷") : MathOperation {
    override fun doUnformattedMath(first: BigDecimal, second: BigDecimal) = first / second
  }
}