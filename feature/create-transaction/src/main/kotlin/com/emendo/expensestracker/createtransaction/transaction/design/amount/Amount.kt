package com.emendo.expensestracker.createtransaction.transaction.design.amount

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.style.TextAlign
import com.emendo.expensestracker.core.designsystem.component.AutoSizableText
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.createtransaction.transaction.ERROR_ANIMATION_DURATION_MILLIS
import com.emendo.expensestracker.createtransaction.transaction.design.amountColor
import com.emendo.expensestracker.createtransaction.transaction.marginHorizontal

@Composable
internal fun Amount(
  text: String,
  transactionType: TransactionType,
  error: Boolean,
  onErrorConsumed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(ERROR_ANIMATION_DURATION_MILLIS),
    label = "error"
  )
  AutoSizableText(
    textProvider = { text },
    minFontSize = MaterialTheme.typography.bodyMedium.fontSize,
    color = transactionType.amountColor(),
    style = MaterialTheme.typography.headlineMedium,
    textAlign = TextAlign.End,
    maxLines = 1,
    modifier = modifier
      .fillMaxWidth()
      .drawBehind { drawRect(backgroundColor.value) }
      .padding(horizontal = marginHorizontal),
  )
}