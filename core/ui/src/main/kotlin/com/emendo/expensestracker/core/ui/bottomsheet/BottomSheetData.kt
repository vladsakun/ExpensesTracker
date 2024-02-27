package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.runtime.Stable

@Stable
interface BottomSheetData {
  val id: String
    get() = "BottomSheetData"
}