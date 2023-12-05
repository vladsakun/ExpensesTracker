package com.emendo.expensestracker.core.ui.bottomsheet.base

import com.emendo.expensestracker.core.model.data.BottomSheetData
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

data class BottomSheetState(
  val bottomSheetState: BottomSheetData? = null,
  val hideBottomSheetEvent: StateEvent = consumed,
  val navigateUpEvent: StateEvent = consumed,
)