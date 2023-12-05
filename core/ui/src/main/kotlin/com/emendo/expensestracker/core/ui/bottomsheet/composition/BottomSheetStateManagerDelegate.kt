package com.emendo.expensestracker.core.ui.bottomsheet.composition

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetState
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BottomSheetStateManagerDelegate<BSType> : BottomSheetStateManager<BSType> {

  private val _bottomSheetState: MutableStateFlow<BottomSheetState<BSType?>> = MutableStateFlow(BottomSheetState())
  override val bottomSheetState: StateFlow<BottomSheetState<BSType?>>
    get() = _bottomSheetState.asStateFlow()

  override fun dismissBottomSheet() {
    onDismissBottomSheet()
    showBottomSheet(null)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  override fun confirmValueChange(sheetValue: SheetValue): Boolean = true

  override fun hideBottomSheet() {
    _bottomSheetState.update { it.copy(hideBottomSheetEvent = triggered) }
  }

  override fun onConsumedHideBottomSheetEvent() {
    _bottomSheetState.update { it.copy(hideBottomSheetEvent = consumed) }
  }

  override fun consumeNavigateUpEvent() {
    _bottomSheetState.update { it.copy(navigateUpEvent = consumed) }
  }

  override fun showBottomSheet(bottomSheet: BSType?) {
    _bottomSheetState.update { it.copy(bottomSheetState = bottomSheet) }
  }

  override fun navigateUp() {
    _bottomSheetState.update { it.copy(navigateUpEvent = triggered) }
  }
}