package com.emendo.expensestracker.core.model.data

interface NumKeyboardActions {
  fun onChangeSignClick()
  fun onCurrencyClick()
  fun onClearClick()
  fun onMathOperationClick(mathOperation: MathOperation)
  fun onNumberClick(numKeyboardNumber: NumKeyboardNumber)
  fun onPrecisionClick()
  fun onDoneClick()
  fun onEqualClick()

  //  data class InitialBalanceActions(
  //    override val onChangeSignClick: () -> Unit,
  //    override val onCurrencyClick: () -> Unit,
  //    override val onClearClick: () -> Unit,
  //    override val onMathOperationClick: (mathOperation: MathOperation) -> Unit,
  //    override val onNumberClick: (numKeyboardNumber: NumKeyboardNumber) -> Unit,
  //    override val onPrecisionClick: () -> Unit,
  //    override val onDoneClick: () -> Unit,
  //    override val onEqualClick: () -> Unit,
  //  ) : NumKeyboardActions {
  //
  //    companion object {
  //      fun dummyInitialBalanceActions() = InitialBalanceActions(
  //        onChangeSignClick = {},
  //        onCurrencyClick = {},
  //        onClearClick = {},
  //        onMathOperationClick = {},
  //        onNumberClick = {},
  //        onPrecisionClick = {},
  //        onDoneClick = {},
  //        onEqualClick = {},
  //      )
  //    }
  //  }
}