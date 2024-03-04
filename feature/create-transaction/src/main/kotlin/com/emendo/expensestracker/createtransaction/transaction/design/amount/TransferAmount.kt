package com.emendo.expensestracker.createtransaction.transaction.design.amount

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.emendo.expensestracker.core.designsystem.component.AutoSizableText

@Composable
internal fun TransferAmount(
  text: String,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.typography.headlineMedium.color,
) {
  AutoSizableText(
    textProvider = { text },
    modifier = modifier,
    minFontSize = MaterialTheme.typography.bodyMedium.fontSize,
    style = MaterialTheme.typography.headlineMedium,
    color = textColor,
    textAlign = TextAlign.End,
    maxLines = 1,
  )
}