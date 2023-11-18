package com.emendo.expensestracker.core.data

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumKeyboardNumber
import java.math.BigDecimal
import javax.inject.Inject

// Todo refactor

class CalculatorInputImpl @Inject constructor(
  private val amountFormatter: AmountFormatter,
) : CalculatorInput {

  override val currentValue: BigDecimal
    get() {
      doMath()
      return amountFormatter.toBigDecimal(number1.toString())
    }

  private var number1: StringBuilder = StringBuilder(DEFAULT_CALCULATOR_TEXT)
  private var number2: StringBuilder? = null
  private var mathOperation: MathOperation? = null
  private var doOnValueChange: (formattedValue: String, equalButtonState: EqualButtonState) -> Unit =
    { _, _ -> throw IllegalStateException("doOnValueChange is not initialized! Initialize it via initCallbacks() method ") }

  private val maxNumberLength = amountFormatter.maxDigitsBeforeDecimal
  private val maxDecimalLength = amountFormatter.maxDigitsAfterDecimal
  private val decimalSeparator = amountFormatter.decimalSeparator
  private val decimalSeparatorString = decimalSeparator.toString()

  private val formatted: String
    get() {
      // Todo refactor
      val isNum1EndsWithDecimalSeparator = number1.endsWithDecimalSeparator()
      val isNum2EndsWithDecimalSeparator = number2.endsWithDecimalSeparator()

      return StringBuilder(formatNumber(number1))
        .append(if (isNum1EndsWithDecimalSeparator) decimalSeparatorString else "")
        .appendIfNotNull(mathOperation?.symbolWithWhiteSpaces)
        .appendIfNotNull(formatNumber(number2))
        .append(if (isNum2EndsWithDecimalSeparator) decimalSeparatorString else "")
        .toString()
    }

  private val equalButtonState: EqualButtonState
    get() = when {
      canDoMath() -> EqualButtonState.Equal
      else -> EqualButtonState.Done
    }

  private val numberToOperate: StringBuilder?
    get() = if (mathOperation == null) number1 else number2

  override fun initCallbacks(callbacks: KeyboardCallbacks) {
    this.doOnValueChange = callbacks::doOnValueChange
  }

  override fun input(mathOperation: MathOperation) {
    this.mathOperation = mathOperation
    refreshValue()
  }

  override fun input(number: NumKeyboardNumber) {
    appendNumber(number)
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

      mathOperation != null -> {
        mathOperation = null
      }

      number1.isBlank() || number1.length == 1 -> {
        number1 = StringBuilder(DEFAULT_CALCULATOR_TEXT)
      }

      number1 != StringBuilder(DEFAULT_CALCULATOR_TEXT) -> {
        number1 = number1.deleteAt(number1.lastIndex)
      }
    }

    refreshValue()
  }

  override fun onDoneClick() {
    doMath(null, true)
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

      return false
    }

    val decimal1 = amountFormatter.toBigDecimal(number1.toString())
    val decimal2 = amountFormatter.toBigDecimal(checkNotNull(number2?.toString()))
    val result = checkNotNull(mathOperation).doMath(decimal1, decimal2)

    number1 = StringBuilder(amountFormatter.formatFinal(result))
    number2 = null
    mathOperation = nextMathOperation
    refreshValue()

    return true
  }

  override fun setNumber1(number: StringBuilder) {
    this.number1 = number
  }

  override fun isEmpty() =
    this.number1.toString() == DEFAULT_CALCULATOR_TEXT && mathOperation == null && number2 == null

  override fun isNotEmpty() = !isEmpty()

  override fun clear() {
    number1 = StringBuilder(DEFAULT_CALCULATOR_TEXT)
    mathOperation = null
    number2 = null
    refreshValue()
  }

  private fun appendNumber(number: NumKeyboardNumber) {
    if (mathOperation == null) {
      appendNum1(number.number.toString())
    } else {
      appendNum2(number.number.toString())
    }
  }

  private fun appendNum1(value: String) {
    if (number1.toString() == DEFAULT_CALCULATOR_TEXT) {
      number1 = StringBuilder(value)
      refreshValue()
      return
    }

    if (!canAppend(number1)) return

    number1.append(value)
    refreshValue()
  }

  private fun appendNum2(value: String) {
    if (number2 == null) {
      number2 = StringBuilder(value)
      refreshValue()
      return
    }

    if (!canAppend(number2)) return

    number2?.append(value)
    refreshValue()
  }

  private fun canAppend(number: StringBuilder?): Boolean {
    if (number == null) return false

    return when {
      number.containsDecimalSeparator() -> {
        val decimalAmount = number.length - number.indexOf(decimalSeparatorString)
        decimalAmount <= maxDecimalLength
      }

      else -> number.length <= maxNumberLength
    }
  }

  private fun StringBuilder.appendDecimalSeparator() {
    if (containsDecimalSeparator().not()) {
      append(decimalSeparatorString)
      refreshValue()
    }
  }

  private fun refreshValue() {
    doOnValueChange.invoke(formatted, equalButtonState)
  }

  private fun formatNumber(stringBuilder: StringBuilder?): String? {
    if (stringBuilder == null) {
      return null
    }

    return when (val string = stringBuilder.toString()) {
      "-" -> "-"
      else -> amountFormatter.format(string)
    }
  }

  private fun canDoMath() = mathOperation != null && number2 != null
  private fun StringBuilder?.endsWithDecimalSeparator() = this?.endsWith(decimalSeparatorString) ?: false
  private fun StringBuilder?.containsDecimalSeparator() = this?.contains(decimalSeparator) ?: false
}