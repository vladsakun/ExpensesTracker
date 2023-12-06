package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

@Stable
data class BottomSheetState(
  val bottomSheetState: BottomSheetData? = null,
  val hideBottomSheetEvent: StateEvent = consumed,
  val navigateUpEvent: StateEvent = consumed,
)