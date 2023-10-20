package com.emendo.expensestracker.core.ui.bottomsheet.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import com.emendo.expensestracker.core.model.data.CalculatorKeyboardActions
import com.emendo.expensestracker.core.model.data.EqualButtonState

@Composable
fun CalculatorBS(
  textState: State<String>,
  currencyState: State<String?>,
  equalButtonState: State<EqualButtonState>,
  actions: CalculatorKeyboardActions,
  decimalSeparator: String,
  source: State<CalculatorTransactionUiModel?>,
  target: State<CalculatorTransactionUiModel?>,
) {
  BaseNumKeyboardBS(
    text = textState,
    equalButtonState = equalButtonState,
    actions = actions,
    decimalSeparator = decimalSeparator,
    firstAction = { modifier ->
      RoundedCornerSmallButton(
        onClick = actions::onChangeSourceClick,
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
        onClick = actions::onCurrencyClick,
        enabled = true,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
      ) {
        currencyState.value?.let {
          Text(
            text = stringResource(id = R.string.currency_with_symbol, it),
            modifier = Modifier.align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(lineHeight = 15.sp),
          )
        }
      }
    },
    headerRows = listOf {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = Dimens.icon_button_size)
          .padding(horizontal = Dimens.margin_small_xx),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TransactionItem(source, actions::onChangeSourceClick)
        Spacer(modifier = Modifier.width(Dimens.margin_small_x))
        Icon(imageVector = ExpeIcons.ArrowForward, contentDescription = null)
        Spacer(modifier = Modifier.width(Dimens.margin_small_x))
        TransactionItem(target, actions::onChangeTargetClick)
      }
    }
  )
}

@Composable
private fun RowScope.TransactionItem(
  calculatorTransactionUiModelState: State<CalculatorTransactionUiModel?>,
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
    calculatorTransactionUiModelState.value?.icon?.let { icon ->
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.align(Alignment.CenterVertically),
      )
    }
    Spacer(modifier = Modifier.width(Dimens.margin_small_x))
    calculatorTransactionUiModelState.value?.name?.let { name ->
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