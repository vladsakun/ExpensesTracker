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
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions

@Stable
interface InitialBalanceKeyboardActions {
  fun onChangeSignClick()
}

@Composable
fun NumericKeyboardBottomSheet(
  textProvider: () -> String,
  currency: String,
  equalButtonStateProvider: () -> EqualButtonState,
  actions: InitialBalanceKeyboardActions,
  numericKeyboardActions: NumericKeyboardActions,
  decimalSeparator: String,
) {
  BaseNumericalKeyboardBottomSheet(
    textProvider = textProvider,
    equalButtonStateProvider = equalButtonStateProvider,
    decimalSeparator = decimalSeparator,
    firstAction = { modifier ->
      RoundedCornerSmallButton(
        onClick = actions::onChangeSignClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = "+/-",
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
        )
      }
    },
    secondAction = { modifier ->
      RoundedCornerSmallButton(
        onClick = {},
        enabled = false,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = modifier,
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
  )
}

//  Column(
//    modifier = Modifier
//      .fillMaxWidth()
//      .padding(Dimens.margin_small_xx)
//      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
//    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
//  ) {
//    Row { ResultText(text::value) }
//    val mathOperationModifier = Modifier
//      .weight(MATH_OPERATION_WEIGHT)
//      .applyKeyboardPadding()
//
//    val digitModifier = Modifier
//      .weight(DIGIT_BUTTON_WEIGHT)
//      .applyKeyboardPadding()
//
//    CalculatorRow {
//      RoundedCornerSmallButton(
//        onClick = actions::onChangeSignClick,
//        colors = ButtonDefaults.filledTonalButtonColors(),
//        modifier = digitModifier,
//        contentPadding = PaddingValues(0.dp),
//      ) {
//        Text(
//          text = "+/-",
//          modifier = Modifier.align(Alignment.CenterVertically),
//          textAlign = TextAlign.Center,
//        )
//      }
//      RoundedCornerSmallButton(
//        onClick = {},
//        enabled = false,
//        colors = ButtonDefaults.filledTonalButtonColors(),
//        modifier = digitModifier,
//        contentPadding = PaddingValues(0.dp),
//      ) {
//        Text(
//          text = stringResource(id = R.string.currency_with_symbol, currency),
//          modifier = Modifier.align(Alignment.CenterVertically),
//          textAlign = TextAlign.Center,
//          style = LocalTextStyle.current.copy(lineHeight = 15.sp),
//        )
//      }
//      RoundedCornerSmallButton(
//        onClick = numericKeyboardActions::onClearClick,
//        colors = ButtonDefaults.filledTonalButtonColors(),
//        modifier = digitModifier,
//      ) {
//        Icon(
//          imageVector = ExpeIcons.Backspace,
//          contentDescription = stringResource(R.string.clear)
//        )
//      }
//      MathOperationButton(
//        onClick = { numericKeyboardActions.onMathOperationClick(MathOperation.Add()) },
//        modifier = mathOperationModifier,
//        contentDescription = stringResource(R.string.add),
//        imageVector = ExpeIcons.Add,
//      )
//    }
//    CalculatorRow {
//      keyboardRows[0].forEach { number ->
//        DigitButton(
//          onClick = { numericKeyboardActions.onNumberClick(number) },
//          text = number.number.toString(),
//        )
//      }
//      MathOperationButton(
//        onClick = { numericKeyboardActions.onMathOperationClick(MathOperation.Substract()) },
//        modifier = mathOperationModifier,
//        contentDescription = stringResource(R.string.substract),
//        imageVector = ExpeIcons.Remove,
//      )
//    }
//    CalculatorRow {
//      keyboardRows[1].forEach { number ->
//        DigitButton(
//          onClick = { numericKeyboardActions.onNumberClick(number) },
//          text = number.number.toString(),
//        )
//      }
//      MathOperationButton(
//        onClick = { numericKeyboardActions.onMathOperationClick(MathOperation.Multiply()) },
//        modifier = mathOperationModifier,
//        contentDescription = stringResource(R.string.multiply),
//        imageVector = ExpeIcons.Close,
//      )
//    }
//    CalculatorRow {
//      keyboardRows[2].forEach { number ->
//        DigitButton(
//          onClick = { numericKeyboardActions.onNumberClick(number) },
//          text = number.number.toString(),
//        )
//      }
//      MathOperationButton(
//        onClick = { numericKeyboardActions.onMathOperationClick(MathOperation.Divide()) },
//        modifier = mathOperationModifier,
//        contentDescription = stringResource(R.string.divide),
//        imageVector = ImageVector.vectorResource(R.drawable.division),
//      )
//    }
//    CalculatorRow {
//      RoundedCornerSmallButton(
//        onClick = numericKeyboardActions::onPrecisionClick,
//        colors = ButtonDefaults.filledTonalButtonColors(),
//        contentPadding = PaddingValues(0.dp),
//        modifier = digitModifier,
//      ) {
//        Text(
//          text = decimalSeparator,
//          modifier = Modifier.align(Alignment.CenterVertically),
//          textAlign = TextAlign.Center,
//        )
//      }
//      keyboardRows[3].forEach { number ->
//        DigitButton(
//          onClick = { numericKeyboardActions.onNumberClick(number) },
//          text = number.number.toString(),
//        )
//      }
//      DoneButton(
//        equalButtonStateProvider = equalButtonState::value,
//        onClick = {
//          when (equalButtonState.value) {
//            EqualButtonState.Done -> numericKeyboardActions.onDoneClick()
//            EqualButtonState.Equal -> numericKeyboardActions.onEqualClick()
//          }
//        },
//      )
//    }
//  }
//}