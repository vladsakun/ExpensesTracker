package com.emendo.expensestracker.core.domain.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.app.resources.models.TransactionElementName

@Stable
data class CalculatorTransactionUiModel(
  val name: TransactionElementName,
  val icon: ImageVector,
  val currency: String? = null, // Todo rethink this
)