package com.emendo.expensestracker.core.ui.bottomsheet.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.AutoSizableText
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.model.data.BaseNumKeyboardActions
import com.emendo.expensestracker.core.model.data.EqualButtonState
import com.emendo.expensestracker.core.model.data.MathOperation
import com.emendo.expensestracker.core.model.data.NumKeyboardNumber

const val MATH_OPERATION_WEIGHT = 3f
const val DIGIT_BUTTON_WEIGHT = 5f

@Composable
fun BaseNumKeyboardBS(
  text: State<String>,
  equalButtonState: State<EqualButtonState>,
  actions: BaseNumKeyboardActions,
  decimalSeparator: String,
  firstAction: @Composable (Modifier) -> Unit,
  secondAction: @Composable (Modifier) -> Unit,
  modifier: Modifier = Modifier,
  headerRows: List<@Composable () -> Unit> = emptyList(),
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
    Box(
      // Todo make fit whole space except keyboard   modifier = Modifier.weight(1f)
    ) {
      headerRows.forEach { it() }
    }

    Row { ResultText(text) }
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
        initialBalanceActions = actions,
        mathOperation = MathOperation.Add(),
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.add),
        imageVector = ExpeIcons.Add,
      )
    }
    CalculatorRow {
      DigitButton(actions, NumKeyboardNumber.Seven(), digitModifier)
      DigitButton(actions, NumKeyboardNumber.Eight(), digitModifier)
      DigitButton(actions, NumKeyboardNumber.Nine(), digitModifier)
      MathOperationButton(
        initialBalanceActions = actions,
        mathOperation = MathOperation.Substract(),
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.substract),
        imageVector = ExpeIcons.Remove,
      )
    }
    CalculatorRow {
      DigitButton(actions, NumKeyboardNumber.Four(), digitModifier)
      DigitButton(actions, NumKeyboardNumber.Five(), digitModifier)
      DigitButton(actions, NumKeyboardNumber.Six(), digitModifier)
      MathOperationButton(
        initialBalanceActions = actions,
        mathOperation = MathOperation.Multiply(),
        modifier = mathOperationModifier,
        contentDescription = stringResource(R.string.multiply),
        imageVector = ExpeIcons.Close,
      )
    }
    CalculatorRow {
      DigitButton(actions, NumKeyboardNumber.One(), digitModifier)
      DigitButton(actions, NumKeyboardNumber.Two(), digitModifier)
      DigitButton(actions, NumKeyboardNumber.Three(), digitModifier)
      MathOperationButton(
        initialBalanceActions = actions,
        mathOperation = MathOperation.Divide(),
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
      DigitButton(actions, NumKeyboardNumber.Zero(), digitModifier)
      DoneButton(equalButtonState, actions)
    }
  }
}

@Composable
fun ResultText(text: State<String>) {
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
fun RowScope.DoneButton(
  equalButtonState: State<EqualButtonState>,
  initialBalanceActions: BaseNumKeyboardActions,
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
fun MathOperationButton(
  initialBalanceActions: BaseNumKeyboardActions,
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
fun DigitButton(
  actions: BaseNumKeyboardActions,
  number: NumKeyboardNumber,
  modifier: Modifier,
) {
  RoundedCornerSmallButton(
    onClick = remember { { actions.onNumberClick(number) } },
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

fun Modifier.applyKeyboardPadding() = this
  .padding(horizontal = Dimens.margin_small_xx)
  .fillMaxHeight()