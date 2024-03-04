package com.emendo.expensestracker.createtransaction.transaction.design.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal inline fun RowScope.TransferColumn(content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier = Modifier.weight(1f),
    verticalArrangement = Arrangement.Center,
    content = content,
  )
}