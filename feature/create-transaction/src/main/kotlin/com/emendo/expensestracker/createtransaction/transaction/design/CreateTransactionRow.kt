package com.emendo.expensestracker.createtransaction.transaction.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.createtransaction.transaction.marginHorizontal
import com.emendo.expensestracker.createtransaction.transaction.marginVertical

@Composable
internal inline fun CreateTransactionRow(
  modifier: Modifier = Modifier,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = horizontalArrangement,
    modifier = modifier.padding(vertical = marginVertical, horizontal = marginHorizontal),
    content = content,
  )
}