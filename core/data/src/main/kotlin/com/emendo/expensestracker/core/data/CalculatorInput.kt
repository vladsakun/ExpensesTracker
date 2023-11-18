package com.emendo.expensestracker.core.data

import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumKeyboardNumber
import java.math.BigDecimal

const val DEFAULT_CALCULATOR_TEXT = "0"

interface CalculatorInput {
  val currentValue: BigDecimal

  fun initCallbacks(callbacks: KeyboardCallbacks)
  fun input(mathOperation: MathOperation)
  fun input(number: NumKeyboardNumber)
  fun onPrecisionClick()
  fun onClearClick()
  fun onDoneClick()

  fun doMath(
    nextMathOperation: MathOperation? = null,
    shouldCleanMathOperationIfCantDoMath: Boolean = false,
  ): Boolean

  fun setNumber1(number: StringBuilder)
  fun isEmpty(): Boolean
  fun isNotEmpty(): Boolean
  fun clear()
}

interface KeyboardCallbacks {
  fun doOnValueChange(formattedValue: String, equalButtonState: EqualButtonState)
}