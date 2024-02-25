package com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.AutoSizableTextField
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.MathOperation
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardNumber
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.NumericKeyboardConstants.DIGIT_BUTTON_WEIGHT
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.NumericKeyboardConstants.MATH_OPERATION_WEIGHT
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.NumericKeyboardConstants.keyboardRows

@Composable
internal fun BaseNumericalKeyboardBottomSheet(
  equalButtonStateProvider: () -> EqualButtonState,
  decimalSeparator: String,
  firstAction: @Composable (Modifier) -> Unit,
  secondAction: @Composable (Modifier) -> Unit,
  onClearClick: () -> Unit,
  onMathOperationClick: (mathOperation: MathOperation) -> Unit,
  onNumberClick: (numericKeyboardNumber: NumericKeyboardNumber) -> Unit,
  onPrecisionClick: () -> Unit,
  onDoneClick: () -> Unit,
  onEqualClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .verticalScroll(rememberScrollState())
      .padding(Dimens.margin_small_xx)
      .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
  ) {
    val mathOperationModifier = remember {
      Modifier
        .weight(MATH_OPERATION_WEIGHT)
        .applyKeyboardPadding()
    }

    val digitModifier = remember {
      Modifier
        .weight(DIGIT_BUTTON_WEIGHT)
        .applyKeyboardPadding()
    }

    CalculatorRow {
      firstAction(digitModifier)
      secondAction(digitModifier)
      RoundedCornerSmallButton(
        onClick = onClearClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = digitModifier,
      ) {
        Icon(
          imageVector = ExpeIcons.Backspace,
          contentDescription = stringResource(R.string.clear)
        )
      }
      MathOperationButton(
        onClick = { onMathOperationClick(MathOperation.Add()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.add),
        imageVector = ExpeIcons.Add,
      )
    }
    CalculatorRow {
      keyboardRows[0].forEach { number ->
        DigitButton(
          text = number.number.toString(),
          onClick = remember { { onNumberClick(number) } },
          modifier = digitModifier,
        )
      }
      MathOperationButton(
        onClick = { onMathOperationClick(MathOperation.Substract()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.substract),
        imageVector = ExpeIcons.Remove,
      )
    }
    CalculatorRow {
      keyboardRows[1].forEach { number ->
        DigitButton(
          text = number.number.toString(),
          onClick = remember { { onNumberClick(number) } },
          modifier = digitModifier,
        )
      }
      MathOperationButton(
        onClick = { onMathOperationClick(MathOperation.Multiply()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.multiply),
        imageVector = ExpeIcons.Close,
      )
    }
    CalculatorRow {
      keyboardRows[2].forEach { number ->
        DigitButton(
          text = number.number.toString(),
          onClick = remember { { onNumberClick(number) } },
          modifier = digitModifier,
        )
      }
      MathOperationButton(
        onClick = { onMathOperationClick(MathOperation.Divide()) },
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.divide),
        imageVector = ImageVector.vectorResource(R.drawable.division),
      )
    }
    CalculatorRow {
      RoundedCornerSmallButton(
        onClick = onPrecisionClick,
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
          text = number.number.toString(),
          onClick = remember { { onNumberClick(number) } },
          modifier = digitModifier,
        )
      }
      DoneButton(
        equalButtonStateProvider = equalButtonStateProvider,
        onClick = {
          when (equalButtonStateProvider()) {
            EqualButtonState.Done -> onDoneClick.invoke()
            EqualButtonState.Equal -> onEqualClick.invoke()
          }
        }
      )
    }
  }
}

@Composable
fun ResultText(textProvider: () -> String) {
  // Todo remove hardcoded value. Use sp to dp converted
  val heightValue = MaterialTheme.typography.headlineLarge.lineHeight.value + 5
  val lineHeightDp = with(LocalDensity.current) { heightValue.sp.toDp() }
  AutoSizableTextField(
    text = textProvider(),
    minFontSize = MaterialTheme.typography.bodyMedium.fontSize,
    textAlign = TextAlign.End,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = Dimens.margin_small_xx)
      .height(lineHeightDp),
    style = MaterialTheme.typography.headlineLarge,
    maxLines = 1,
    focused = true,
  )
}

@Composable
private fun RowScope.DoneButton(
  equalButtonStateProvider: () -> EqualButtonState,
  onClick: () -> Unit,
) {
  RoundedCornerSmallButton(
    onClick = onClick,
    colors = ButtonDefaults.filledTonalButtonColors(),
    contentPadding = PaddingValues(0.dp),
    modifier = Modifier
      .weight(DIGIT_BUTTON_WEIGHT + MATH_OPERATION_WEIGHT)
      .applyKeyboardPadding(),
  ) {
    when (equalButtonStateProvider()) {
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
  onClick: () -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
  imageVector: ImageVector? = null,
) {
  RoundedCornerSmallButton(
    onClick = onClick,
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
  onClick: () -> Unit,
  text: String,
  modifier: Modifier = Modifier,
) {
  RoundedCornerSmallButton(
    onClick = onClick,
    modifier = modifier,
    contentPadding = PaddingValues(0.dp),
  ) {
    Text(
      text = text,
      textAlign = TextAlign.Center,
      modifier = Modifier.align(Alignment.CenterVertically),
    )
  }
}

@Composable
private fun CalculatorRow(
  modifier: Modifier = Modifier,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  content: @Composable (RowScope.() -> Unit),
) {
  Row(
    modifier = modifier.height(Dimens.icon_button_size),
    content = content,
    horizontalArrangement = horizontalArrangement,
  )
}

private fun Modifier.applyKeyboardPadding() = this
  .padding(horizontal = Dimens.margin_small_xx)
  .fillMaxHeight()