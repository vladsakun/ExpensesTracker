package com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.NumericKeyboardActions

@Composable
fun NumericKeyboardBottomSheet(
  currency: String,
  equalButtonStateProvider: () -> EqualButtonState,
  actions: InitialBalanceKeyboardActions,
  numericKeyboardActions: NumericKeyboardActions,
  decimalSeparator: String,
  modifier: Modifier = Modifier,
) {
  BaseNumericalKeyboardBottomSheet(
    // Todo fix
    equalButtonStateProvider = equalButtonStateProvider,
    decimalSeparator = decimalSeparator,
    firstAction = { actionModifier ->
      RoundedCornerSmallButton(
        onClick = actions::onChangeSignClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = actionModifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = "+/-",
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
        )
      }
    },
    secondAction = { actionModifier ->
      RoundedCornerSmallButton(
        onClick = {},
        enabled = false,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = actionModifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = stringResource(id = R.string.currency_with_symbol, currency),
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
          style = LocalTextStyle.current.copy(lineHeight = 15.sp),
        )
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