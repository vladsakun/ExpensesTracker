package com.emendo.expensestracker.core.app.resources.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
data class CalculatorTransactionUiModel(
  val name: String,
  val icon: ImageVector,
  val currency: String? = null, // Todo rethink this
)