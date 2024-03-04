package com.emendo.expensestracker.createtransaction.transaction.design

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.createtransaction.transaction.CreateTransactionUiState
import com.emendo.expensestracker.data.api.model.transaction.TransactionType

@Composable
@ReadOnlyComposable
internal fun TransactionType.amountColor(): Color {
  if (this == TransactionType.INCOME) {
    return MaterialTheme.customColorsPalette.successColor
  }

  return LocalTextStyle.current.color
}

@Composable
@ReadOnlyComposable
internal fun CreateTransactionUiState.transferTextColor(): Color =
  if (isCustomTransferAmount) {
    MaterialTheme.customColorsPalette.successColor
  } else {
    MaterialTheme.colorScheme.outline
  }