package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ModalBottomSheetStateManager {

  val modalBottomSheetState: StateFlow<BottomSheetState>

  fun onDismissModalBottomSheet() {}
  fun dismissModalBottomSheet()
  fun hideModalBottomSheet()
  fun onConsumedHideModalBottomSheetEvent()
  fun consumeNavigateUpEvent()
  fun showModalBottomSheet(bottomSheet: BottomSheetData?)
  fun navigateUp()

  @OptIn(ExperimentalMaterial3Api::class)
  fun confirmValueChange(
    sheetValue: SheetValue,
    bottomSheetState: BottomSheetData? = modalBottomSheetState.value.bottomSheetData,
  ): Boolean = true
}