package com.emendo.expensestracker.core.data

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.model.data.EqualButtonState
import com.emendo.expensestracker.core.model.data.MathOperation
import com.emendo.expensestracker.core.model.data.NumKeyboardNumber

class CalculatorBSInput(
  private var number1: StringBuilder,
  private val amountFormatter: AmountFormatter,
  private val doOnValueChange: (formattedValue: String, equalButtonState: EqualButtonState) -> Unit,
  private var mathOperation: MathOperation? = null,
) {
  private var number2: StringBuilder? = null
  private val maxNumberLength = amountFormatter.maxDigitsBeforeDecimal
  private val maxDecimalLength = amountFormatter.maxDigitsAfterDecimal
  private val decimalSeparator = amountFormatter.decimalSeparator
  private val decimalSeparatorString = decimalSeparator.toString()

  private val formatted: String
    get() {
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

  fun input(mathOperation: MathOperation) {
    this.mathOperation = mathOperation
    refreshValue()
  }

  fun input(number: NumKeyboardNumber) {
    appendNumber(number)
  }

  fun addDecimalSeparator() {
    if (mathOperation == null) {
      appendDecimalSeparatorNum1()
    } else {
      appendDecimalSeparatorNum2()
    }
  }

  fun onClearClick() {
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
        number1 = StringBuilder(DEFAULT_INITIAL_BALANCE)
      }

      number1 != StringBuilder(DEFAULT_INITIAL_BALANCE) -> {
        number1 = number1.deleteAt(number1.lastIndex)
      }
    }

    refreshValue()
  }

  fun doMath(
    nextMathOperation: MathOperation? = null,
    shouldCleanMathOperationIfCantDoMath: Boolean = false,
  ): Boolean {
    if (!canDoMath()) {
      if (shouldCleanMathOperationIfCantDoMath) {
        mathOperation = null
        refreshValue()
      }

      return false
    }

    val decimal1 = number1.toString().toBigDecimal()
    val decimal2 = checkNotNull(number2).toString().toBigDecimal()

    val result = checkNotNull(mathOperation).doMath(decimal1, decimal2)
      .toString()
      .dropLastWhile { it == '0' }
      .dropLastWhile { it == decimalSeparatorString.last() }
    number1 = StringBuilder(result)
    number2 = null
    mathOperation = nextMathOperation
    refreshValue()

    return true
  }

  fun doMathAndCleanMathOperation(nextMathOperation: MathOperation? = null): Boolean {
    return doMath(nextMathOperation, shouldCleanMathOperationIfCantDoMath = true)
  }

  private fun appendNumber(number: NumKeyboardNumber) {
    if (mathOperation == null) {
      appendNum1(number.number.toString())
    } else {
      appendNum2(number.number.toString())
    }
  }

  private fun appendNum1(value: String) {
    if (number1.toString() == DEFAULT_INITIAL_BALANCE) {
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

  private fun appendDecimalSeparatorNum1() {
    if (number1.containsDecimalSeparator().not()) {
      number1.append(decimalSeparatorString)
      refreshValue()
    }
  }

  private fun appendDecimalSeparatorNum2() {
    if (number2.containsDecimalSeparator().not()) {
      number2?.append(decimalSeparatorString)
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
  private fun StringBuilder?.containsDecimalSeparator() = amountFormatter.containsDecimalSeparator(this)

  companion object {
    const val DEFAULT_INITIAL_BALANCE = "0"
  }
}