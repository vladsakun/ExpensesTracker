package com.emendo.expensestracker.core.ui

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType

@Composable
fun AmountTextResizable(
  amount: Amount,
  textStyle: TextStyle,
  modifier: Modifier = Modifier,
  transactionType: TransactionType? = null,
  textAlign: TextAlign? = null,
  textColor: Color? = null,
) {
  val defaultColor = LocalContentColor.current
  val color: Color = when {
    textColor != null -> textColor

    transactionType == null -> if (amount.value.signum() == -1) {
      defaultColor
    } else {
      MaterialTheme.customColorsPalette.successColor
    }

    else -> if (transactionType == TransactionType.INCOME) {
      MaterialTheme.customColorsPalette.successColor
    } else {
      defaultColor
    }
  }
  BasicText(
    text = amount.formattedValue,
    style = textStyle.merge(
      color = color,
      textAlign = textAlign ?: TextAlign.Unspecified,
    ),
    autoSize = TextAutoSize.StepBased(
      minFontSize = 12.sp,
      maxFontSize = textStyle.fontSize
    ),
    modifier = modifier,
  )
}