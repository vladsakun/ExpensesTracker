package com.emendo.expensestracker.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType

@Composable
fun AmountText(
  amount: Amount,
  textStyle: TextStyle,
  modifier: Modifier = Modifier,
  transactionType: TransactionType? = null,
  textAlign: TextAlign? = null,
  textColor: Color? = null,
) {
  val color: Color = when {
    textColor != null -> textColor

    transactionType == null -> if (amount.value.signum() == -1) {
      Color.Unspecified
    } else {
      MaterialTheme.customColorsPalette.successColor
    }

    else -> if (transactionType == TransactionType.INCOME) {
      MaterialTheme.customColorsPalette.successColor
    } else {
      Color.Unspecified
    }
  }
  Text(
    text = amount.formattedValue,
    style = textStyle,
    color = color,
    textAlign = textAlign,
    modifier = modifier,
  )
}