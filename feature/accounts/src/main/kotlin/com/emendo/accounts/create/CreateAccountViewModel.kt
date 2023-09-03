package com.emendo.accounts.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.AmountFormatter
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.EqualButtonState
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.MathOperation
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.NumKeyboardNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

val INITIAL_NUM1 = StringBuilder("0")
fun StringBuilder.appendIfNotNull(value: String?): StringBuilder = if (value != null) this.append(value) else this

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val amountFormatter: AmountFormatter,
) : ViewModel() {

  private val _state =
    MutableStateFlow(
      CreateAccountScreenData.getDefaultState(
        defaultCurrency = CurrencyModel.USD,
        decimalSeparator = amountFormatter.decimalSeparator.toString(),
      )
    )
  val state = _state.asStateFlow()

  private var input = Input(
    number1 = INITIAL_NUM1,
    mathOperation = null,
    number2 = null,
    amountFormatter = amountFormatter,
    doOnValueChange = { formattedValue, equalButtonState ->
      _state.update {
        it.copy(
          initialBalance = formattedValue,
          equalButtonState = equalButtonState,
        )
      }
    }
  )

  class Input(
    mathOperation: MathOperation?,
    var number1: StringBuilder,
    var number2: StringBuilder?,
    val amountFormatter: AmountFormatter,
    val doOnValueChange: (formattedValue: String, equalButtonState: EqualButtonState) -> Unit,
  ) {
    var mMathOperation: MathOperation? = mathOperation
      set(value) {
        field = value
        refreshValue()
      }

    private val maxNumberLength = amountFormatter.maxDigitsBeforeDecimal
    private val maxDecimalLength = amountFormatter.maxDigitsAfterDecimal
    private val decimalSeparator = amountFormatter.decimalSeparator.toString()

    fun appendNumber(number: NumKeyboardNumber) {
      if (mMathOperation == null) {
        appendNum1(number.number.toString())
      } else {
        appendNum2(number.number.toString())
      }
    }

    fun addDecimalSeparator() {
      if (mMathOperation == null) {
        appendDecimalNum1()
      } else {
        appendDecimalNum2()
      }
    }

    fun appendDecimalNum1() {
      if (!amountFormatter.containsDecimal(this.number1)) {
        this.number1 = this.number1.append(decimalSeparator)
        refreshValue()
      }
    }

    fun appendDecimalNum2() {
      if (!amountFormatter.containsDecimal(this.number2)) {
        this.number2 = this.number2?.append(decimalSeparator)
        refreshValue()
      }
    }

    fun appendNum1(value: String) {
      if (this.number1.toString() == INITIAL_NUM1.toString()) {
        this.number1 = StringBuilder(value)
        refreshValue()
        return
      }

      if (!canAppend(this.number1)) return

      this.number1 = this.number1.append(value)
      refreshValue()
    }

    fun appendNum2(value: String) {
      if (this.number2 == null) {
        this.number2 = StringBuilder(value)
        refreshValue()
        return
      }

      if(!canAppend(this.number2)) return

      this.number2 = this.number2?.append(value)
      refreshValue()
    }

    private fun canAppend(number: StringBuilder?): Boolean {
      if (amountFormatter.containsDecimal(number)) {
        val decimalAmount = (number?.length ?: 0) - (number?.indexOf(decimalSeparator) ?: 0)
        if (decimalAmount > maxDecimalLength) return false
      } else {
        if ((number?.length ?: 0) > maxNumberLength) return false
      }

      return true
    }

    val formatted: String
      get() {
        val isNum1EndWithDelimiter = this.number1.endsWith(decimalSeparator)
        val isNum2EndWithDelimiter = this.number2?.endsWith(decimalSeparator) ?: false

        val formattedNum1 = formatNumber(this.number1)

        return StringBuilder(formattedNum1)
          .append(if (isNum1EndWithDelimiter) decimalSeparator else "")
          .appendIfNotNull(mMathOperation?.symbolWithWhiteSpaces)
          .appendIfNotNull(formatNumber(this.number2))
          .append(if (isNum2EndWithDelimiter) decimalSeparator else "")
          .toString()
      }

    val equalButtonState: EqualButtonState
      get() = when {
        canDoMath() -> EqualButtonState.Equal
        else -> EqualButtonState.Done
      }

    fun onClearClick() {
      var shouldUpdateFormattedValue = true
      when {
        this.number2 != null -> {
          this.number2 = when (this.number2!!.length) {
            1 -> null
            else -> this.number2?.deleteAt(this.number2!!.lastIndex)
          }
        }

        mMathOperation != null -> {
          shouldUpdateFormattedValue = false
          mMathOperation = null
        }

        this.number1.isBlank() || this.number1.length == 1 -> this.number1 = INITIAL_NUM1
        this.number1 != INITIAL_NUM1 -> this.number1 = this.number1.deleteAt(this.number1.lastIndex)
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

      val decimal2 = checkNotNull(this.number2).toString().toBigDecimal()
      val decimal1 = this.number1.toString().toBigDecimal()

      val result = checkNotNull(mMathOperation).doMath(decimal1, decimal2)
      this.number1 = StringBuilder(result.toString())
      this.number2 = null
      mMathOperation = nextMathOperation

      return true
    }

    fun doOnDoneClick(nextMathOperation: MathOperation? = null): Boolean {
      return doMath(nextMathOperation, shouldCleanMathOperationIfCantDoMath = true)
    }

    private fun canDoMath() = mMathOperation != null && this.number2 != null
  }

  fun setAccountName(accountName: String) {
    _state.update {
      it.copy(
        accountName = accountName,
        isCreateAccountButtonEnabled = accountName.isNotBlank(),
      )
    }
  }

  fun setCurrency(currency: CurrencyModel) {
    _state.update { it.copy(currency = currency) }
  }

  fun setIcon(icon: AccountIconModel) {
    _state.update { it.copy(icon = icon) }
  }

  fun setColor(color: ColorModel) {
    _state.update { it.copy(color = color) }
  }

  fun onChangeSignClick() {
  }

  fun onClearClick() {
    input.onClearClick()
  }

  fun onMathOperationClick(mathOperation: MathOperation) {
    if (input.doMath(mathOperation)) return

    input.mMathOperation = mathOperation
  }

  fun onNumberClick(number: NumKeyboardNumber) {
    input.appendNumber(number)
  }

  fun onPrecisionClick() {
    input.addDecimalSeparator()
  }

  fun onConfirmClick() {
    input.doOnDoneClick()
  }

  fun onEqualClick() {
    input.doMath()
  }

  fun createNewAccount() {
    viewModelScope.launch {
      with(state.value) {
        accountsRepository.upsertAccount(
          Account(
            name = accountName,
            balance = amountFormatter.toAmount(initialBalance, currency.currencyName).toBigDecimal().toDouble(),
            currencyModel = currency,
            icon = icon,
            color = color,
          )
        )
      }
    }
    //    viewModelScope.launch {
    //      accountsRepository.upsertAccount(
    //        Account(
    //          name = accountName,
    //          balance = initialBalance,
    //          currencyModel = currencyModel,
    //          icon = icon,
    //          color = color
    //        )
    //      )
    //    }

    // Todo
  }

  fun onCurrencyClick() {

  }

}