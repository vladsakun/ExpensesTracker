package com.emendo.expensestracker.core.data.helper

import com.emendo.expensestracker.core.data.CalculatorInput
import com.emendo.expensestracker.core.data.DEFAULT_CALCULATOR_TEXT
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumKeyboardNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import javax.inject.Inject

class CalculatorCommanderImpl @Inject constructor(
  private val calculatorInput: CalculatorInput,
) : CalculatorCommander {

  override val calculatorTextState = MutableStateFlow(DEFAULT_CALCULATOR_TEXT)
  override val equalButtonState = MutableStateFlow(EqualButtonState.Default)
  override val currencyValue: BigDecimal
    get() = calculatorInput.currentValue

  private var doneClick: () -> Unit = {
    throw IllegalStateException("doneClick shouldn't be with default value. Override it via setCallbacks()")
  }
  private var clearCallback: () -> Boolean = { false }
  private var mathOperationClick: (mathOperation: MathOperation) -> Boolean = { false }
  private var numberClick: (numKeyboardNumber: NumKeyboardNumber) -> Boolean = { false }
  private var precisionClick: () -> Boolean = { false }
  private var equalClick: () -> Boolean = { false }
  private var valueChanged: (formattedValue: String, equalButtonState: EqualButtonState) -> Boolean = { _, _ -> false }

  init {
    calculatorInput.initCallbacks(this)
  }

  override fun setCallbacks(
    doneClick: () -> Unit,
    clear: () -> Boolean,
    mathOperationClick: (mathOperation: MathOperation) -> Boolean,
    numberClick: (numKeyboardNumber: NumKeyboardNumber) -> Boolean,
    precisionClick: () -> Boolean,
    equalClick: () -> Boolean,
    valueChanged: (formattedValue: String, equalButtonState: EqualButtonState) -> Boolean,
  ) {
    this.doneClick = doneClick
    this.clearCallback = clear
    this.mathOperationClick = mathOperationClick
    this.numberClick = numberClick
    this.precisionClick = precisionClick
    this.equalClick = equalClick
    this.valueChanged = valueChanged
  }

  override fun onDoneClick() {
    doneClick()
  }

  override fun doOnValueChange(formattedValue: String, equalButtonState: EqualButtonState) {
    if (valueChanged(formattedValue, equalButtonState)) {
      return
    }

    calculatorTextState.update { formattedValue }
    this.equalButtonState.update { equalButtonState }
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

    calculatorInput.input(mathOperation)
  }

  override fun onNumberClick(numKeyboardNumber: NumKeyboardNumber) {
    if (numberClick(numKeyboardNumber)) {
      return
    }

    calculatorInput.input(numKeyboardNumber)
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
}