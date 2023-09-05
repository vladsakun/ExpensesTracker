package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.runtime.Stable

@Stable
enum class EqualButtonState {
  Equal,
  Done;

  companion object {
    val Default = Done
  }
}