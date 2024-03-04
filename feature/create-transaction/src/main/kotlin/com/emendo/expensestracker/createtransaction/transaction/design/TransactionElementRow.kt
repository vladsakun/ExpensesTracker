package com.emendo.expensestracker.createtransaction.transaction.design

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.PlaceholderTextStyle
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.createtransaction.transaction.ERROR_ANIMATION_DURATION_MILLIS
import com.emendo.expensestracker.createtransaction.transaction.TransactionItemModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color

@Composable
internal fun TransactionElementRow(
  transactionItem: TransactionItemModel?,
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  error: Boolean = false,
  onErrorConsumed: () -> Unit = {},
) {
  val backgroundColor = animateColorAsState(
    targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
    finishedListener = { onErrorConsumed() },
    animationSpec = tween(ERROR_ANIMATION_DURATION_MILLIS),
    label = "error"
  )
  CreateTransactionRow(
    modifier = modifier
      .clickable(onClick = onClick)
      .drawBehind { drawRect(backgroundColor.value) },
  ) {
    Text(
      text = label,
      style = PlaceholderTextStyle,
    )
    Spacer(modifier = Modifier.width(Dimens.margin_small_x))
    Spacer(modifier = Modifier.weight(1f))
    transactionItem?.let { model ->
      TransactionElement(
        icon = model.icon.imageVector,
        title = model.name.stringValue(),
        tint = model.color.color,
      )
    }
  }
  ExpeDivider()
}

@Composable
private fun TransactionElement(
  icon: ImageVector,
  title: String,
  tint: Color,
) {
  Icon(
    imageVector = icon,
    contentDescription = null,
    tint = tint,
  )
  Spacer(modifier = Modifier.width(Dimens.margin_small_xx))
  Text(
    text = title,
    textAlign = TextAlign.End,
    overflow = TextOverflow.Ellipsis,
    maxLines = 1,
  )
}