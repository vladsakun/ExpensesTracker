package com.emendo.expensestracker.createtransaction.transaction.design.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.createtransaction.transaction.TRANSFER_BLOCK_MIN_HEIGHT
import com.emendo.expensestracker.createtransaction.transaction.marginHorizontal
import com.emendo.expensestracker.createtransaction.transaction.marginVertical

@Composable
internal inline fun TransferRow(content: @Composable RowScope.() -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = TRANSFER_BLOCK_MIN_HEIGHT.dp)
      .padding(horizontal = marginHorizontal, vertical = marginVertical),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(marginVertical),
    content = content,
  )
}