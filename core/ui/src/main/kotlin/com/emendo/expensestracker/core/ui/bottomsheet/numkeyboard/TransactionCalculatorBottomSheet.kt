package com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.model.data.keyboard.CalculatorConstants
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions

@Stable
data class CalculatorBottomSheetState(
  val text: String,
  val currency: String?,
  val equalButtonState: EqualButtonState,
  val decimalSeparator: String,
  val numericKeyboardActions: NumericKeyboardActions,
) {
  companion object {
    fun initial(
      decimalSeparator: String,
      numericKeyboardActions: NumericKeyboardActions,
    ) = CalculatorBottomSheetState(
      text = CalculatorConstants.INITIAL_CALCULATOR_TEXT,
      currency = null,
      equalButtonState = EqualButtonState.Default,
      decimalSeparator = decimalSeparator,
      numericKeyboardActions = numericKeyboardActions,
    )
  }
}

@Stable
interface CalculatorKeyboardActions {
  fun changeTransactionType()
  fun onCurrencyClick()
}

@Composable
fun TransactionCalculatorBottomSheet(
  textStateProvider: () -> String,
  currencyState: () -> String?,
  equalButtonState: () -> EqualButtonState,
  decimalSeparator: String,
  calculatorActions: CalculatorKeyboardActions,
  numericKeyboardActions: NumericKeyboardActions,
  modifier: Modifier = Modifier,
) {
  BaseNumericalKeyboardBottomSheet(
    textProvider = textStateProvider,
    equalButtonStateProvider = equalButtonState,
    decimalSeparator = decimalSeparator,
    firstAction = { actionModifier ->
      RoundedCornerSmallButton(
        onClick = calculatorActions::changeTransactionType,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = actionModifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = "Income",
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
        )
      }
    },
    secondAction = { actionModifier ->
      RoundedCornerSmallButton(
        onClick = calculatorActions::onCurrencyClick,
        enabled = true,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = actionModifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        currencyState()?.let { currency ->
          Text(
            text = stringResource(id = R.string.currency_with_symbol, currency),
            modifier = Modifier.align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(lineHeight = 15.sp),
          )
        }
      }
    },
    onClearClick = numericKeyboardActions::onClearClick,
    onMathOperationClick = numericKeyboardActions::onMathOperationClick,
    onNumberClick = numericKeyboardActions::onNumberClick,
    onPrecisionClick = numericKeyboardActions::onPrecisionClick,
    onDoneClick = numericKeyboardActions::onDoneClick,
    onEqualClick = numericKeyboardActions::onEqualClick,
    modifier = modifier,
  )
}