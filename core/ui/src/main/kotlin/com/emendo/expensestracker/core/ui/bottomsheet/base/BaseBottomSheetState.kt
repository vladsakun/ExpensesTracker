package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.runtime.Stable
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

@Stable
data class BaseBottomSheetState<BSType>(
  val bottomSheetState: BSType? = null,
  val hideBottomSheetEvent: StateEvent = consumed,
  val navigateUpEvent: StateEvent = consumed,
)