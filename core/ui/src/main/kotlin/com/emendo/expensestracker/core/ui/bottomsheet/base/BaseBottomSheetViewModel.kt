package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.ViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Todo use composition over inheritance
abstract class BaseBottomSheetViewModel<BSType> : ViewModel() {

  private val _bottomSheetState = MutableStateFlow<BottomSheetState<BSType?>>(BottomSheetState())
  val bottomSheetState = _bottomSheetState.asStateFlow()

  open fun dismissBottomSheet() {
    showBottomSheet(null)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  open fun confirmValueChange(sheetValue: SheetValue): Boolean = true

  fun hideBottomSheet() {
    _bottomSheetState.update { it.copy(hideBottomSheetEvent = triggered) }
  }

  open fun onConsumedHideBottomSheetEvent() {
    _bottomSheetState.update { it.copy(hideBottomSheetEvent = consumed) }
  }

  fun consumeNavigateUpEvent() {
    _bottomSheetState.update { it.copy(navigateUpEvent = consumed) }
  }

  protected fun showBottomSheet(bottomSheet: BSType?) {
    _bottomSheetState.update { it.copy(bottomSheetState = bottomSheet) }
  }

  protected fun navigateUp() {
    _bottomSheetState.update { it.copy(navigateUpEvent = triggered) }
  }

}