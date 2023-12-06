package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import kotlinx.coroutines.flow.StateFlow

@Stable
interface BottomSheetStateManager {

  val bottomSheetState: StateFlow<BottomSheetState>

  fun onDismissBottomSheet() {}
  fun dismissBottomSheet()
  fun hideBottomSheet()
  fun onConsumedHideBottomSheetEvent()
  fun consumeNavigateUpEvent()
  fun showBottomSheet(bottomSheet: BottomSheetData?)
  fun navigateUp()

  @OptIn(ExperimentalMaterial3Api::class)
  fun confirmValueChange(sheetValue: SheetValue): Boolean = true
}