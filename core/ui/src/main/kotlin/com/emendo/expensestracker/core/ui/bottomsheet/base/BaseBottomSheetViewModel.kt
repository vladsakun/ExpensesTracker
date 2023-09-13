package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.lifecycle.ViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseBottomSheetViewModel<BSType> : ViewModel() {

  private val _bottomSheetState = MutableStateFlow<BaseBottomSheetState<BSType?>>(BaseBottomSheetState())
  val bottomSheetState = _bottomSheetState.asStateFlow()

  open fun onDismissBottomSheetRequest() {
    updateBottomSheet(null)
  }

  fun hideBottomSheet() {
    _bottomSheetState.update { it.copy(hideBottomSheetEvent = triggered) }
  }

  fun onConsumedHideBottomSheetEvent() {
    _bottomSheetState.update { it.copy(hideBottomSheetEvent = consumed) }
  }

  fun onConsumedNavigateUpEvent() {
    _bottomSheetState.update { it.copy(navigateUpEvent = consumed) }
  }

  protected fun updateBottomSheet(bottomSheet: BSType?) {
    _bottomSheetState.update { it.copy(bottomSheetState = bottomSheet) }
  }

  protected fun navigateUp() {
    _bottomSheetState.update { it.copy(navigateUpEvent = triggered) }
  }

}