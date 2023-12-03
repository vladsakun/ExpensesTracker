package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuAction(
  val icon: ImageVector,
  val onClick: () -> Unit,
  val text: String,
  val enabled: Boolean = true,
)