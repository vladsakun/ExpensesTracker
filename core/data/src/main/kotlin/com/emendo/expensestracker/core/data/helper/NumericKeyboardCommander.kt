package com.emendo.expensestracker.core.data.helper

import com.emendo.expensestracker.core.data.KeyboardCallbacks
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardNumber
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

interface NumericKeyboardCommander : NumericKeyboardActions, KeyboardCallbacks {
  val calculatorTextState: StateFlow<String>
  val equalButtonState: StateFlow<EqualButtonState>
  val currencyValue: BigDecimal

  fun setCallbacks(
    doneClick: () -> Boolean,
    clear: () -> Boolean = { false },
    mathOperationClick: (mathOperation: MathOperation) -> Boolean = { false },
    numberClick: (numericKeyboardNumber: NumericKeyboardNumber) -> Boolean = { false },
    precisionClick: () -> Boolean = { false },
    equalClick: () -> Boolean = { false },
    valueChanged: (formattedValue: String, equalButtonState: EqualButtonState) -> Boolean = { _, _ -> false },
    onMathDone: (String) -> Unit = { },
  )

  fun clear()
  fun isNotEmpty(): Boolean
  fun getCalculatorValue(): String
  fun doMath()
}