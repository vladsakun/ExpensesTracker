package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardNumber

@Stable
interface NumericKeyboardActions {
  fun onClearClick()
  fun onMathOperationClick(mathOperation: MathOperation)
  fun onNumberClick(numericKeyboardNumber: NumericKeyboardNumber)
  fun onPrecisionClick()
  fun onDoneClick()
  fun onEqualClick()
}