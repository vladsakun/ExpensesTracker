package com.emendo.expensestracker.core.model.data

interface BaseNumKeyboardActions {
  fun onClearClick()
  fun onMathOperationClick(mathOperation: MathOperation)
  fun onNumberClick(numKeyboardNumber: NumKeyboardNumber)
  fun onPrecisionClick()
  fun onDoneClick()
  fun onEqualClick()
}

interface InitialBalanceKeyboardActions : BaseNumKeyboardActions {
  fun onChangeSignClick()
}

interface CalculatorKeyboardActions : BaseNumKeyboardActions {
  fun onChangeSourceClick()
  fun onChangeTargetClick()
  fun onCurrencyClick()
}