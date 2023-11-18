package com.emendo.expensestracker.core.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.emendo.expensestracker.core.designsystem.component.ExpOverlayLoadingWheel

@Composable
fun LoadingDialog() {
  Dialog(onDismissRequest = { /*TODO*/ }) {
    ExpOverlayLoadingWheel(contentDesc = "Loading")
  }
}