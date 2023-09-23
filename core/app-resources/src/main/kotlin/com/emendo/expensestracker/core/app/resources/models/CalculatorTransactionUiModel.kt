package com.emendo.expensestracker.core.app.resources.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.model.data.TransactionElement

@Stable
data class CalculatorTransactionUiModel(
  val name: String,
  val icon: ImageVector,
  val element: TransactionElement,
)