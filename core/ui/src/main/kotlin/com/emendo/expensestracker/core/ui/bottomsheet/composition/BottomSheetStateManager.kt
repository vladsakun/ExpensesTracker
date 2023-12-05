package com.emendo.expensestracker.core.ui.bottomsheet.composition

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetState
import kotlinx.coroutines.flow.StateFlow

interface BottomSheetStateManager<BSType> {

  val bottomSheetState: StateFlow<BottomSheetState<BSType?>>

  fun onDismissBottomSheet() {}
  fun dismissBottomSheet()
  fun hideBottomSheet()
  fun onConsumedHideBottomSheetEvent()
  fun consumeNavigateUpEvent()
  fun showBottomSheet(bottomSheet: BSType?)
  fun navigateUp()

  @OptIn(ExperimentalMaterial3Api::class)
  fun confirmValueChange(sheetValue: SheetValue): Boolean = true
}