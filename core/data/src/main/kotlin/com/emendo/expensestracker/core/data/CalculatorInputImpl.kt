package com.emendo.expensestracker.core.data

import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.model.data.keyboard.CalculatorConstants.INITIAL_CALCULATOR_TEXT
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardNumber
import java.math.BigDecimal
import javax.inject.Inject

class CalculatorInputImpl @Inject constructor(
  private val calculatorFormatter: CalculatorFormatter,
) : CalculatorInput {

  override val currentValue: BigDecimal
    get() {
      doMath()
      return calculatorFormatter.toBigDecimal(number1.toString())
    }

  private val initialCalculatorText by lazy(LazyThreadSafetyMode.NONE) { StringBuilder(INITIAL_CALCULATOR_TEXT) }
  private val minusSymbol by lazy(LazyThreadSafetyMode.NONE) { "-" }

  // State
  private var number1: StringBuilder = initialCalculatorText
  private var number2: StringBuilder? = null
  private var mathOperation: MathOperation? = null

  // Callbacks
  private var doOnValueChange: (formattedValue: String, equalButtonState: EqualButtonState) -> Unit =
    { _, _ -> throw IllegalStateException("doOnValueChange is not initialized! Initialize it via initCallbacks() method ") }
  private var onMathDone: (String) -> Unit = { }

  private val maxNumberLength = calculatorFormatter.maxDigitsBeforeDecimal
  private val maxDecimalLength = calculatorFormatter.maxDigitsAfterDecimal
  private val decimalSeparator = calculatorFormatter.decimalSeparator
  private val decimalSeparatorString = decimalSeparator.toString()

  private val formatted: String
    get() {
      return buildString {
        appendFormattedNumber(number1)
        appendIfNotNull(mathOperation?.symbolWithWhiteSpaces)
        appendFormattedNumber(number2)
      }
    }

  private val equalButtonState: EqualButtonState
    get() = if (canDoMath()) EqualButtonState.Equal else EqualButtonState.Done

  private val numberToOperate: StringBuilder?
    get() = if (mathOperation == null) number1 else number2

  override fun initCallbacks(callbacks: KeyboardCallbacks) {
    doOnValueChange = callbacks::doOnValueChange
    onMathDone = callbacks::onMathDone
  }

  override fun input(operation: MathOperation) {
    mathOperation = operation
    refreshValue()
  }

  override fun input(number: NumericKeyboardNumber) {
    if (mathOperation == null) {
      appendNum1(number.number.toString())
    } else {
      appendNum2(number.number.toString())
    }
  }

  override fun onPrecisionClick() {
    numberToOperate?.appendDecimalSeparator()
  }

  override fun onClearClick() {
    when {
      number2 != null -> {
        number2 = when (number2!!.length) {
          1 -> null
          else -> number2?.deleteAt(number2!!.lastIndex)
        }
      }

      mathOperation != null -> mathOperation = null
      number1.isBlank() || number1.length == 1 -> number1 = initialCalculatorText
      number1 != initialCalculatorText -> number1 = number1.deleteAt(number1.lastIndex)
    }

    refreshValue()
  }

  override fun onDoneClick() {
    doMath(nextMathOperation = null, shouldCleanMathOperationIfCantDoMath = true)
  }

  override fun doMath(
    nextMathOperation: MathOperation?,
    shouldCleanMathOperationIfCantDoMath: Boolean,
  ): Boolean {
    if (!canDoMath()) {
      if (shouldCleanMathOperationIfCantDoMath) {
        mathOperation = null
        refreshValue()
      }

      onMathDone(number1.toString())
      return false
    }

    val decimal1 = calculatorFormatter.toBigDecimal(number1.toString())
    val decimal2 = calculatorFormatter.toBigDecimal(checkNotNull(number2).toString())
    val result = checkNotNull(mathOperation).doMath(decimal1, decimal2)

    number1 = StringBuilder(calculatorFormatter.formatFinal(result))
    number2 = null
    mathOperation = nextMathOperation
    refreshValue()
    onMathDone(number1.toString())

    return true
  }

  override fun getMathResult(): BigDecimal {
    val decimal1 = calculatorFormatter.toBigDecimal(number1.toString())

    if (!canDoMath()) {
      return decimal1
    }

    val decimal2 = calculatorFormatter.toBigDecimal(checkNotNull(number2).toString())

    return checkNotNull(mathOperation).doMath(decimal1, decimal2)
  }

  override fun setNumber1(number: StringBuilder) {
    number1 = number
    refreshValue()
  }

  override fun isEmpty(): Boolean {
    return number1.toString() == INITIAL_CALCULATOR_TEXT && mathOperation == null && number2 == null
  }

  override fun isNotEmpty() = !isEmpty()

  override fun clear() {
    number1 = StringBuilder(INITIAL_CALCULATOR_TEXT)
    mathOperation = null
    number2 = null
    refreshValue()
  }

  override fun negate(): String {
    val number1 = StringBuilder(calculatorFormatter.formatFinal(currentValue.negate()))
    setNumber1(number1)
    return number1.toString()
  }

  private fun appendNum1(value: String) {
    if (number1.toString() == INITIAL_CALCULATOR_TEXT) {
      number1 = StringBuilder(value)
      refreshValue()
      return
    }

    if (!canAppend(number1)) {
      return
    }

    number1.append(value)
    refreshValue()
  }

  private fun appendNum2(value: String) {
    if (number2 == null) {
      number2 = StringBuilder(value)
      refreshValue()
      return
    }

    if (!canAppend(number2)) {
      return
    }

    number2?.append(value)
    refreshValue()
  }

  private fun canAppend(number: StringBuilder?): Boolean {
    if (number == null) {
      return false
    }

    return when {
      number.containsDecimalSeparator() -> {
        val decimalAmount = number.length - number.indexOf(decimalSeparatorString)
        decimalAmount <= maxDecimalLength
      }

      else -> number.length <= maxNumberLength
    }
  }

  private fun StringBuilder.appendDecimalSeparator() {
    if (!containsDecimalSeparator()) {
      append(decimalSeparatorString)
      refreshValue()
    }
  }

  override fun refreshValue() {
    doOnValueChange.invoke(formatted, equalButtonState)
  }

  private fun formatNumber(stringBuilder: StringBuilder?): String? {
    if (stringBuilder == null) {
      return null
    }

    return when (val string = stringBuilder.toString()) {
      minusSymbol -> minusSymbol
      else -> calculatorFormatter.format(string)
    }
  }

  private fun canDoMath(): Boolean {
    return mathOperation != null && number2 != null
  }

  private fun StringBuilder?.endsWithDecimalSeparator(): Boolean {
    return this?.endsWith(decimalSeparatorString) ?: false
  }

  private fun StringBuilder?.containsDecimalSeparator(): Boolean {
    return this?.contains(decimalSeparator) ?: false
  }

  private fun StringBuilder.appendFormattedNumber(number: StringBuilder?) {
    if (number == null) {
      return
    }

    append(formatNumber(number))
    if (number.endsWithDecimalSeparator()) append(decimalSeparatorString)
  }
}