package com.emendo.expensestracker.core.ui.bottomsheet.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions

@Stable
interface InitialBalanceKeyboardActions : NumericKeyboardActions {
  fun onChangeSignClick()
}

@Composable
fun InitialBalanceBS(
  text: State<String>,
  currency: String,
  equalButtonState: State<EqualButtonState>,
  actions: InitialBalanceKeyboardActions,
  decimalSeparator: String,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(Dimens.margin_small_xx)
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
  ) {
    Row { ResultText(text::value) }
    val mathOperationModifier = Modifier
      .weight(MATH_OPERATION_WEIGHT)
      .applyKeyboardPadding()

    val digitModifier = Modifier
      .weight(DIGIT_BUTTON_WEIGHT)
      .applyKeyboardPadding()

    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = actions::onChangeSignClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = digitModifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = "+/-",
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
        )
      }
      RoundedCornerSmallButton(
        onClick = {},
        enabled = false,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = digitModifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = stringResource(id = R.string.currency_with_symbol, currency),
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
          style = LocalTextStyle.current.copy(lineHeight = 15.sp),
        )
      }
      RoundedCornerSmallButton(
        onClick = actions::onClearClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = digitModifier,
      ) {
        Icon(
          imageVector = ExpeIcons.Backspace,
          contentDescription = stringResource(R.string.clear)
        )
      }
      MathOperationButton(
        onClick = { actions.onMathOperationClick(MathOperation.Add()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.add),
        imageVector = ExpeIcons.Add,
      )
    }
    CalculatorRow {
      keyboardRows[0].forEach { number ->
        DigitButton(
          onClick = { actions.onNumberClick(number) },
          text = number.number.toString(),
        )
      }
      MathOperationButton(
        onClick = { actions.onMathOperationClick(MathOperation.Substract()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.substract),
        imageVector = ExpeIcons.Remove,
      )
    }
    CalculatorRow {
      keyboardRows[1].forEach { number ->
        DigitButton(
          onClick = { actions.onNumberClick(number) },
          text = number.number.toString(),
        )
      }
      MathOperationButton(
        onClick = { actions.onMathOperationClick(MathOperation.Multiply()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.multiply),
        imageVector = ExpeIcons.Close,
      )
    }
    CalculatorRow {
      keyboardRows[2].forEach { number ->
        DigitButton(
          onClick = { actions.onNumberClick(number) },
          text = number.number.toString(),
        )
      }
      MathOperationButton(
        onClick = { actions.onMathOperationClick(MathOperation.Divide()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.divide),
        imageVector = ImageVector.vectorResource(R.drawable.division),
      )
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = actions::onPrecisionClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        contentPadding = PaddingValues(0.dp),
        modifier = digitModifier,
      ) {
        Text(
          text = decimalSeparator,
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
        )
      }
      keyboardRows[3].forEach { number ->
        DigitButton(
          onClick = { actions.onNumberClick(number) },
          text = number.number.toString(),
        )
      }
      DoneButton(
        equalButtonStateProvider = equalButtonState::value,
        onClick = {
          when (equalButtonState.value) {
            EqualButtonState.Done -> actions.onDoneClick()
            EqualButtonState.Equal -> actions.onEqualClick()
          }
        },
      )
    }
  }
}