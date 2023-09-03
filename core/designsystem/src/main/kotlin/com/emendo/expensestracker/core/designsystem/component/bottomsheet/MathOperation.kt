package com.emendo.expensestracker.core.designsystem.component.bottomsheet

import java.math.BigDecimal

sealed interface MathOperation {
  val symbol: String
  val symbolWithWhiteSpaces: String
    get() = " $symbol "

  fun doMath(first: BigDecimal, second: BigDecimal): BigDecimal

  data class Add(override val symbol: String = "+") : MathOperation {
    override fun doMath(first: BigDecimal, second: BigDecimal) = first + second
  }

  data class Substract(override val symbol: String = "-") : MathOperation {
    override fun doMath(first: BigDecimal, second: BigDecimal) = first - second
  }

  data class Multiply(override val symbol: String = "×") : MathOperation {
    override fun doMath(first: BigDecimal, second: BigDecimal) = first * second
  }

  data class Divide(override val symbol: String = "÷") : MathOperation {
    override fun doMath(first: BigDecimal, second: BigDecimal) = first / second
  }

  companion object {
    //    fun String.toMathOperation() = first().toMathOperation()

    fun Char.toMathOperation(): MathOperation =
      when (this) {
        '+' -> Add()
        '-' -> Substract()
        '×' -> Multiply()
        '÷' -> Divide()
        else -> throw IllegalArgumentException("Unknown math operation")
      }
  }
}