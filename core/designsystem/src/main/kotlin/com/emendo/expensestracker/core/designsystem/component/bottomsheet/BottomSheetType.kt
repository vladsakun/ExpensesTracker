package com.emendo.expensestracker.core.designsystem.component.bottomsheet

sealed interface ExpeBottomSheetType {
  data class Error(val errorText: String) : ExpeBottomSheetType
  data object Initial : ExpeBottomSheetType
}