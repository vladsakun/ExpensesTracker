package com.emendo.expensestracker.core.app.base.helper

import com.emendo.expensestracker.core.data.CalculatorInput
import com.emendo.expensestracker.core.model.data.keyboard.CalculatorConstants.INITIAL_CALCULATOR_TEXT
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import javax.inject.Inject

class NumericKeyboardCommanderImpl @Inject constructor(
  private val calculatorInput: CalculatorInput,
) : NumericKeyboardCommander {

  override val calculatorTextState by lazy { MutableStateFlow(INITIAL_CALCULATOR_TEXT) }
  override val equalButtonState by lazy { MutableStateFlow(EqualButtonState.Default) }
  override val currentValue: BigDecimal
    get() = calculatorInput.currentValue

  private var doneClick: () -> Boolean = {
    throw IllegalStateException("doneClick shouldn't be with default value. Override it via setCallbacks()")
  }
  private var clearCallback: () -> Boolean = { false }
  private var mathOperationClick: (mathOperation: MathOperation) -> Boolean = { false }
  private var numberClick: (numericKeyboardNumber: NumericKeyboardNumber) -> Boolean = { false }
  private var precisionClick: () -> Boolean = { false }
  private var equalClick: () -> Boolean = { false }
  private var valueChanged: (formattedValue: String, equalButtonState: EqualButtonState) -> Boolean = { _, _ -> false }
  private var mathDone: (String) -> Unit = { }

  init {
    calculatorInput.initCallbacks(this)
  }

  override fun setCallbacks(
    doneClick: () -> Boolean,
    clear: () -> Boolean,
    mathOperationClick: (mathOperation: MathOperation) -> Boolean,
    numberClick: (numericKeyboardNumber: NumericKeyboardNumber) -> Boolean,
    precisionClick: () -> Boolean,
    equalClick: () -> Boolean,
    valueChanged: (formattedValue: String, equalButtonState: EqualButtonState) -> Boolean,
    onMathDone: (String) -> Unit,
  ) {
    this.doneClick = doneClick
    this.clearCallback = clear
    this.mathOperationClick = mathOperationClick
    this.numberClick = numberClick
    this.precisionClick = precisionClick
    this.equalClick = equalClick
    this.valueChanged = valueChanged
    this.mathDone = onMathDone
  }

  override fun onDoneClick() {
    if (doneClick()) {
      return
    }

    calculatorInput.doMath(null, true)
  }

  override fun doOnValueChange(formattedValue: String, equalButtonState: EqualButtonState) {
    if (valueChanged(formattedValue, equalButtonState)) {
      return
    }

    calculatorTextState.update { formattedValue }
    this.equalButtonState.update { equalButtonState }
  }

  override fun onMathDone(result: String) {
    mathDone(result)
  }

  override fun onClearClick() {
    if (clearCallback()) {
      return
    }

    calculatorInput.onClearClick()
  }

  override fun onMathOperationClick(mathOperation: MathOperation) {
    if (mathOperationClick(mathOperation)) {
      return
    }

    if (calculatorInput.doMath(mathOperation)) {
      return
    }

    calculatorInput.input(mathOperation)
  }

  override fun onNumberClick(numericKeyboardNumber: NumericKeyboardNumber) {
    if (numberClick(numericKeyboardNumber)) {
      return
    }

    calculatorInput.input(numericKeyboardNumber)
  }

  override fun onPrecisionClick() {
    if (precisionClick()) {
      return
    }

    calculatorInput.onPrecisionClick()
  }

  override fun onEqualClick() {
    if (equalClick()) {
      return
    }

    calculatorInput.doMath()
  }

  override fun clear() {
    calculatorInput.clear()
  }

  override fun isNotEmpty(): Boolean {
    return calculatorInput.isNotEmpty()
  }

  override fun getCalculatorValue(): String {
    return calculatorTextState.value
  }

  override fun doMath() {
    calculatorInput.doMath()
  }

  override fun negate() {
    calculatorInput.doMath()
    mathDone(calculatorInput.negate())
  }

  override fun setInitialValue(initialValue: String) {
    calculatorInput.setNumber1(StringBuilder(initialValue))
  }

  override fun getMathResult(): BigDecimal =
    calculatorInput.getMathResult()
}