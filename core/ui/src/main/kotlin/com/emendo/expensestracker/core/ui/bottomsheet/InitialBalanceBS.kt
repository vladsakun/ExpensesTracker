package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.designsystem.component.AutoSizableText
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.model.data.MathOperation
import com.emendo.expensestracker.core.model.data.NumKeyboardActions
import com.emendo.expensestracker.core.model.data.NumKeyboardNumber
import com.emendo.expensestracker.core.ui.CalculatorRow

private const val MATH_OPERATION_WEIGHT = 3f
private const val DIGIT_BUTTON_WEIGHT = 5f

@Composable
fun InitialBalanceBS(
  text: State<String>,
  currency: String,
  equalButtonState: State<EqualButtonState>,
  initialBalanceActions: NumKeyboardActions,
  decimalSeparator: String,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(Dimens.margin_small_xx)
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
  ) {
    Row { ResultText(text) }
    val mathOperationModifier = Modifier
      .weight(MATH_OPERATION_WEIGHT)
      .applyKeyboardPadding()

    val digitModifier = Modifier
      .weight(DIGIT_BUTTON_WEIGHT)
      .applyKeyboardPadding()

    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = initialBalanceActions::onChangeSignClick,
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
        onClick = initialBalanceActions::onCurrencyClick,
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
        onClick = initialBalanceActions::onClearClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = digitModifier,
      ) {
        Icon(
          imageVector = ExpIcons.Backspace,
          contentDescription = stringResource(R.string.clear)
        )
      }
      MathOperationButton(
        initialBalanceActions = initialBalanceActions,
        mathOperation = MathOperation.Add(),
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.add),
        imageVector = ExpIcons.Add,
      )
    }
    CalculatorRow {
      DigitButton(initialBalanceActions, NumKeyboardNumber.Seven(), digitModifier)
      DigitButton(initialBalanceActions, NumKeyboardNumber.Eight(), digitModifier)
      DigitButton(initialBalanceActions, NumKeyboardNumber.Nine(), digitModifier)
      MathOperationButton(
        initialBalanceActions = initialBalanceActions,
        mathOperation = MathOperation.Substract(),
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.substract),
        imageVector = ExpIcons.Remove,
      )
    }
    CalculatorRow {
      DigitButton(initialBalanceActions, NumKeyboardNumber.Four(), digitModifier)
      DigitButton(initialBalanceActions, NumKeyboardNumber.Five(), digitModifier)
      DigitButton(initialBalanceActions, NumKeyboardNumber.Six(), digitModifier)
      MathOperationButton(
        initialBalanceActions = initialBalanceActions,
        mathOperation = MathOperation.Multiply(),
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.multiply),
        imageVector = ExpIcons.Close,
      )
    }
    CalculatorRow {
      DigitButton(initialBalanceActions, NumKeyboardNumber.One(), digitModifier)
      DigitButton(initialBalanceActions, NumKeyboardNumber.Two(), digitModifier)
      DigitButton(initialBalanceActions, NumKeyboardNumber.Three(), digitModifier)
      MathOperationButton(
        initialBalanceActions = initialBalanceActions,
        mathOperation = MathOperation.Divide(),
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.divide),
        imageVector = ImageVector.vectorResource(R.drawable.division),
      )
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = initialBalanceActions::onPrecisionClick,
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
      DigitButton(initialBalanceActions, NumKeyboardNumber.Zero(), digitModifier)
      DoneButton(equalButtonState, initialBalanceActions)
    }
  }
}

@Composable
private fun ResultText(text: State<String>) {
  val heightValue = MaterialTheme.typography.headlineLarge.lineHeight.value + 5 // Todo remove hardcoded value
  val lineHeightDp = with(LocalDensity.current) { heightValue.sp.toDp() }
  AutoSizableText(
    text = text.value,
    minFontSize = 14.sp,
    textAlign = TextAlign.End,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = Dimens.margin_small_xx)
      .height(lineHeightDp),
    textStyle = MaterialTheme.typography.headlineLarge,
    maxLines = 1,
  )
}

@Composable
private fun RowScope.DoneButton(
  equalButtonState: State<EqualButtonState>,
  initialBalanceActions: NumKeyboardActions,
) {
  RoundedCornerSmallButton(
    onClick = {
      when (equalButtonState.value) {
        EqualButtonState.Done -> initialBalanceActions.onDoneClick()
        EqualButtonState.Equal -> initialBalanceActions.onEqualClick()
      }
    },
    colors = ButtonDefaults.filledTonalButtonColors(),
    contentPadding = PaddingValues(0.dp),
    modifier = Modifier
      .weight(DIGIT_BUTTON_WEIGHT + MATH_OPERATION_WEIGHT)
      .applyKeyboardPadding(),
  ) {
    when (equalButtonState.value) {
      EqualButtonState.Equal -> Icon(
        painter = painterResource(id = R.drawable.equal),
        contentDescription = stringResource(id = R.string.equal)
      )

      EqualButtonState.Done -> Text(
        text = stringResource(id = R.string.done),
        modifier = Modifier.align(Alignment.CenterVertically),
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun MathOperationButton(
  initialBalanceActions: NumKeyboardActions,
  mathOperation: MathOperation,
  modifier: Modifier,
  contentDescription: String,
  imageVector: ImageVector? = null,
) {
  RoundedCornerSmallButton(
    onClick = { initialBalanceActions.onMathOperationClick(mathOperation) },
    modifier = modifier,
    contentPadding = PaddingValues(0.dp),
    colors = ButtonDefaults.filledTonalButtonColors(),
  ) {
    imageVector?.let {
      Icon(
        imageVector = it,
        contentDescription = contentDescription,
      )
    }
  }
}

@Composable
private fun DigitButton(
  initialBalanceActions: NumKeyboardActions,
  number: NumKeyboardNumber,
  modifier: Modifier,
) {
  RoundedCornerSmallButton(
    onClick = { initialBalanceActions.onNumberClick(number) },
    modifier = modifier,
    contentPadding = PaddingValues(0.dp),
  ) {
    Text(
      text = number.number.toString(),
      textAlign = TextAlign.Center,
      modifier = Modifier.align(Alignment.CenterVertically),
    )
  }
}

private fun Modifier.applyKeyboardPadding() = this
  .padding(horizontal = Dimens.margin_small_xx)
  .fillMaxHeight()