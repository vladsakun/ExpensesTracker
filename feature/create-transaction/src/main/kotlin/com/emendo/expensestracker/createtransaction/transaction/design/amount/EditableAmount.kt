package com.emendo.expensestracker.createtransaction.transaction.design.amount

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.emendo.expensestracker.core.designsystem.component.AutoSizableTextField

@Composable
internal fun EditableAmount(
  text: String,
  focused: Boolean,
  modifier: Modifier = Modifier,
) {
  AutoSizableTextField(
    text = text,
    minFontSize = MaterialTheme.typography.labelSmall.fontSize,
    textAlign = TextAlign.End,
    style = MaterialTheme.typography.labelSmall,
    maxLines = 1,
    focused = focused,
    modifier = modifier,
  )
}