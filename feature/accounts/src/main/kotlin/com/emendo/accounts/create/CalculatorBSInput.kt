package com.emendo.accounts.create

import com.emendo.expensestracker.core.app.common.result.AmountFormatter
import com.emendo.expensestracker.core.app.common.result.appendIfNotNull
import com.emendo.expensestracker.core.model.data.EqualButtonState
import com.emendo.expensestracker.core.model.data.MathOperation
import com.emendo.expensestracker.core.model.data.NumKeyboardNumber

class CalculatorBSInput(
  var number1: StringBuilder,
  mathOperation: MathOperation? = null,
  val amountFormatter: AmountFormatter,
  val doOnValueChange: (formattedValue: String, equalButtonState: EqualButtonState) -> Unit,
) {
  var mMathOperation: MathOperation? = mathOperation
    set(value) {
      field = value
      refreshValue()
    }

  private var number2: StringBuilder? = null
  private val maxNumberLength = amountFormatter.maxDigitsBeforeDecimal
  private val maxDecimalLength = amountFormatter.maxDigitsAfterDecimal
  private val decimalSeparator = amountFormatter.decimalSeparator.toString()

  fun endNumber(number: NumKeyboardNumber) {
    if (mMathOperation == null) {
      endNum1(number.number.toString())
    } else {
      endNum2(number.number.toString())
    }
  }

  fun addDecimalSeparator() {
    if (mMathOperation == null) {
      endDecimalNum1()
    } else {
      endDecimalNum2()
    }
  }

  private fun endDecimalNum1() {
    if (!amountFormatter.containsDecimal(number1)) {
      number1 = number1.append(decimalSeparator)
      refreshValue()
    }
  }

  private fun endDecimalNum2() {
    if (!amountFormatter.containsDecimal(number2)) {
      number2 = number2?.append(decimalSeparator)
      refreshValue()
    }
  }

  private fun endNum1(value: String) {
    if (number1.toString() == CreateAccountScreenData.DEFAULT_INITIAL_BALANCE) {
      number1 = StringBuilder(value)
      refreshValue()
      return
    }

    if (!canend(number1)) return

    number1 = number1.append(value)
    refreshValue()
  }

  private fun endNum2(value: String) {
    if (number2 == null) {
      number2 = StringBuilder(value)
      refreshValue()
      return
    }

    if (!canend(number2)) return

    number2 = number2?.append(value)
    refreshValue()
  }

  private fun canend(number: StringBuilder?): Boolean {
    if (amountFormatter.containsDecimal(number)) {
      val decimalAmount = (number?.length ?: 0) - (number?.indexOf(decimalSeparator) ?: 0)
      if (decimalAmount > maxDecimalLength) return false
    } else {
      if ((number?.length ?: 0) > maxNumberLength) return false
    }

    return true
  }

  private val formatted: String
    get() {
      val isNum1EndWithDelimiter = number1.endsWith(decimalSeparator)
      val isNum2EndWithDelimiter = number2?.endsWith(decimalSeparator) ?: false

      val formattedNum1 = formatNumber(number1)

      return StringBuilder(formattedNum1)
        .append(if (isNum1EndWithDelimiter) decimalSeparator else "")
        .appendIfNotNull(mMathOperation?.symbolWithWhiteSpaces)
        .appendIfNotNull(formatNumber(number2))
        .append(if (isNum2EndWithDelimiter) decimalSeparator else "")
        .toString()
    }

  private val equalButtonState: EqualButtonState
    get() = when {
      canDoMath() -> EqualButtonState.Equal
      else -> EqualButtonState.Done
    }

  fun onClearClick() {
    var shouldUpdateFormattedValue = true
    when {
      number2 != null -> {
        number2 = when (number2!!.length) {
          1 -> null
          else -> number2?.deleteAt(number2!!.lastIndex)
        }
      }

      mMathOperation != null -> {
        shouldUpdateFormattedValue = false
        mMathOperation = null
      }

      number1.isBlank() || number1.length == 1 -> number1 =
        StringBuilder(CreateAccountScreenData.DEFAULT_INITIAL_BALANCE)

      number1 != StringBuilder(CreateAccountScreenData.DEFAULT_INITIAL_BALANCE) -> number1 =
        number1.deleteAt(number1.lastIndex)
    }

    if (shouldUpdateFormattedValue) refreshValue()
  }

  private fun refreshValue() {
    doOnValueChange.invoke(formatted, equalButtonState)
  }

  private fun formatNumber(stringBuilder: StringBuilder?) =
    when (val string = stringBuilder?.toString()) {
      null -> null
      "-" -> "-"
      else -> amountFormatter.format(
        amountFormatter.toAmount(string, currency = "USD"),
        includeCurrency = false
      )
    }

  fun doMath(
    nextMathOperation: MathOperation? = null,
    shouldCleanMathOperationIfCantDoMath: Boolean = false,
  ): Boolean {
    if (!canDoMath()) {
      if (shouldCleanMathOperationIfCantDoMath) {
        mMathOperation = null
      }

      return false
    }

    val decimal2 = checkNotNull(number2).toString().toBigDecimal()
    val decimal1 = number1.toString().toBigDecimal()

    val result = checkNotNull(mMathOperation).doMath(decimal1, decimal2)
    number1 = StringBuilder(result.toString())
    number2 = null
    mMathOperation = nextMathOperation

    return true
  }

  fun doOnDoneClick(nextMathOperation: MathOperation? = null): Boolean {
    return doMath(nextMathOperation, shouldCleanMathOperationIfCantDoMath = true)
  }

  private fun canDoMath() = mMathOperation != null && number2 != null
}