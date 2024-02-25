package com.emendo.expensestracker.data.api

import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardNumber
import java.math.BigDecimal

interface CalculatorInput {
  val currentValue: BigDecimal

  fun initCallbacks(callbacks: KeyboardCallbacks)
  fun input(operation: MathOperation)
  fun input(number: NumericKeyboardNumber)
  fun onPrecisionClick()
  fun onClearClick()
  fun onDoneClick()

  fun doMath(
    nextMathOperation: MathOperation? = null,
    shouldCleanMathOperationIfCantDoMath: Boolean = false,
  ): Boolean

  fun getMathResult(): BigDecimal
  fun setNumber1(number: StringBuilder)
  fun isEmpty(): Boolean
  fun isNotEmpty(): Boolean
  fun clear()
  fun negate(): String
  fun refreshValue()
}

