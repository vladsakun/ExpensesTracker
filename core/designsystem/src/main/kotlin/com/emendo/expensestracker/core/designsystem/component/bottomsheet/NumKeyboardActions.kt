package com.emendo.expensestracker.core.designsystem.component.bottomsheet

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
sealed interface NumKeyboardActions : Parcelable {
  val onChangeSignClick: () -> Unit
  val onCurrencyClick: () -> Unit
  val onClearClick: () -> Unit
  val onMathOperationClick: (mathOperation: MathOperation) -> Unit
  val onNumberClick: (numKeyboardNumber: NumKeyboardNumber) -> Unit
  val onPrecisionClick: () -> Unit
  val onDoneClick: () -> Unit
  val onEqualClick: () -> Unit

  @Parcelize
  @Stable
  data class InitialBalanceActions(
    override val onChangeSignClick: () -> Unit,
    override val onCurrencyClick: () -> Unit,
    override val onClearClick: () -> Unit,
    override val onMathOperationClick: (mathOperation: MathOperation) -> Unit,
    override val onNumberClick: (numKeyboardNumber: NumKeyboardNumber) -> Unit,
    override val onPrecisionClick: () -> Unit,
    override val onDoneClick: () -> Unit,
    override val onEqualClick: () -> Unit,
  ) : NumKeyboardActions {

    companion object {
      fun dummyInitialBalanceActions() = InitialBalanceActions(
        onChangeSignClick = {},
        onCurrencyClick = {},
        onClearClick = {},
        onMathOperationClick = {},
        onNumberClick = {},
        onPrecisionClick = {},
        onDoneClick = {},
        onEqualClick = {},
      )
    }
  }
}