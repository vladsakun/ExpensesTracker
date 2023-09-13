package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import com.emendo.expensestracker.core.designsystem.utils.ExpeBottomSheetShape
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <BottomSheetType> ExpeModalBottomSheet(
  modalBottomSheetState: SheetState,
  bottomSheetState: State<BaseBottomSheetState<BottomSheetType?>>,
  closeBottomSheet: () -> Unit,
  bottomSheetContent: @Composable (type: BottomSheetType?, closeBottomSheet: () -> Unit) -> Unit,
) {
  val shouldOpenBottomSheet = remember { derivedStateOf { bottomSheetState.value.bottomSheetState != null } }
  val focusManager = LocalFocusManager.current

  if (shouldOpenBottomSheet.value) {
    focusManager.clearFocus()
    ModalBottomSheet(
      onDismissRequest = {},
      sheetState = modalBottomSheetState,
      windowInsets = WindowInsets(0),
      shape = ExpeBottomSheetShape,
      content = {
        bottomSheetContent(bottomSheetState.value.bottomSheetState, closeBottomSheet)
      },
    )
  }
}