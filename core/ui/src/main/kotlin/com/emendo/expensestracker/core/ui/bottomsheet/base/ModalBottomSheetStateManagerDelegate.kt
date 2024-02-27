package com.emendo.expensestracker.core.ui.bottomsheet.base

import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ModalBottomSheetStateManagerDelegate : ModalBottomSheetStateManager {

  private val _modalBottomSheetState: MutableStateFlow<BottomSheetState> = MutableStateFlow(BottomSheetState())
  override val modalBottomSheetState: StateFlow<BottomSheetState> = _modalBottomSheetState.asStateFlow()

  override fun dismissModalBottomSheet() {
    onDismissModalBottomSheet()
    showModalBottomSheet(null)
  }

  override fun hideModalBottomSheet() {
    _modalBottomSheetState.update { it.copy(hideBottomSheetEvent = triggered) }
  }

  override fun onConsumedHideModalBottomSheetEvent() {
    _modalBottomSheetState.update { it.copy(hideBottomSheetEvent = consumed) }
  }

  override fun consumeNavigateUpEvent() {
    _modalBottomSheetState.update { it.copy(navigateUpEvent = consumed) }
  }

  override fun showModalBottomSheet(bottomSheet: BottomSheetData?) {
    _modalBottomSheetState.update { it.copy(bottomSheetData = bottomSheet) }
  }

  override fun navigateUp() {
    _modalBottomSheetState.update { it.copy(navigateUpEvent = triggered) }
  }
}