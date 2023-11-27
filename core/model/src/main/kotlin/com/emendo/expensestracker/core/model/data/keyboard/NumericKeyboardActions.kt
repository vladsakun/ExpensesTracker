package com.emendo.expensestracker.core.model.data.keyboard

interface NumericKeyboardActions {
  fun onClearClick()
  fun onMathOperationClick(mathOperation: MathOperation)
  fun onNumberClick(numericKeyboardNumber: NumericKeyboardNumber)
  fun onPrecisionClick()
  fun onDoneClick()
  fun onEqualClick()
}