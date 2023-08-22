package com.emendo.expensestracker.core.designsystem.utils

import androidx.compose.ui.Modifier

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
  if (condition) this.modifier() else this