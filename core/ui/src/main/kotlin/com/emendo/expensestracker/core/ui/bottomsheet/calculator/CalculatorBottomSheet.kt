package com.emendo.expensestracker.core.ui.bottomsheet.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.designsystem.component.RoundedCornerSmallButton
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.model.data.keyboard.CalculatorConstants
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions
import kotlinx.collections.immutable.persistentListOf

@Stable
data class CalculatorBottomSheetState(
  val text: String,
  val currency: String?,
  val equalButtonState: EqualButtonState,
  val source: CalculatorTransactionUiModel?,
  val target: CalculatorTransactionUiModel?,
) {
  companion object {
    fun initial() = CalculatorBottomSheetState(
      text = CalculatorConstants.DEFAULT_CALCULATOR_TEXT,
      currency = null,
      equalButtonState = EqualButtonState.Default,
      source = null,
      target = null,
    )
  }
}

@Stable
interface CalculatorKeyboardActions {
  fun onChangeSourceClick()
  fun changeTarget()
  fun onCurrencyClick()
}

@Composable
fun CalculatorBottomSheet(
  textStateProvider: () -> String,
  currencyState: () -> String?,
  equalButtonState: () -> EqualButtonState,
  decimalSeparator: String,
  source: () -> CalculatorTransactionUiModel?,
  target: () -> CalculatorTransactionUiModel?,
  calculatorActions: CalculatorKeyboardActions,
  numericKeyboardActions: NumericKeyboardActions,
) {
  BaseNumericalKeyboardBottomSheet(
    textProvider = textStateProvider,
    equalButtonStateProvider = equalButtonState,
    decimalSeparator = decimalSeparator,
    firstAction = { modifier ->
      RoundedCornerSmallButton(
        onClick = calculatorActions::onChangeSourceClick,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        Text(
          text = "Income",
          modifier = Modifier.align(Alignment.CenterVertically),
          textAlign = TextAlign.Center,
        )
      }
    },
    secondAction = { modifier ->
      RoundedCornerSmallButton(
        onClick = calculatorActions::onCurrencyClick,
        enabled = true,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = modifier,
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
    headerRows = persistentListOf(
      {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.icon_button_size)
            .padding(horizontal = Dimens.margin_small_xx),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TransactionItem(source, calculatorActions::onChangeSourceClick)
          Spacer(modifier = Modifier.width(Dimens.margin_small_x))
          Icon(imageVector = ExpeIcons.ArrowForward, contentDescription = null)
          Spacer(modifier = Modifier.width(Dimens.margin_small_x))
          TransactionItem(target, calculatorActions::changeTarget)
        }
      }
    ),
    onClearClick = numericKeyboardActions::onClearClick,
    onMathOperationClick = numericKeyboardActions::onMathOperationClick,
    onNumberClick = numericKeyboardActions::onNumberClick,
    onPrecisionClick = numericKeyboardActions::onPrecisionClick,
    onDoneClick = numericKeyboardActions::onDoneClick,
    onEqualClick = numericKeyboardActions::onEqualClick,
  )
}

@Composable
private fun RowScope.TransactionItem(
  transactionUiModelProvider: () -> CalculatorTransactionUiModel?,
  onClick: () -> Unit,
) {
  RoundedCornerSmallButton(
    onClick = onClick,
    colors = ButtonDefaults.filledTonalButtonColors(),
    modifier = Modifier
      .heightIn(min = Dimens.icon_button_size)
      .weight(1f),
    contentPadding = PaddingValues(0.dp),
  ) {
    transactionUiModelProvider()?.icon?.let { icon ->
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.align(Alignment.CenterVertically),
      )
    }
    Spacer(modifier = Modifier.width(Dimens.margin_small_x))
    transactionUiModelProvider()?.name?.let { name ->
      Text(
        text = name,
        modifier = Modifier.Companion.align(Alignment.CenterVertically),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
      )
    }
  }
}