package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFocusManager
import com.emendo.expensestracker.core.designsystem.utils.ExpeBottomSheetShape
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetState

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun <BottomSheetType> ExpeModalBottomSheet(
  modalBottomSheetState: SheetState,
  bottomSheetState: () -> BaseBottomSheetState<BottomSheetType?>,
  hideBottomSheet: () -> Unit,
  onDismissRequest: () -> Unit,
  bottomSheetContent: @Composable (type: BottomSheetType?, hideBottomSheet: () -> Unit) -> Unit,
) {
  val shouldOpenBottomSheet = remember { derivedStateOf { bottomSheetState().bottomSheetState != null } }
  val focusManager = LocalFocusManager.current

  if (shouldOpenBottomSheet.value) {
    focusManager.clearFocus()
    ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = modalBottomSheetState,
      // Todo uncomment when fixed https://issuetracker.google.com/issues/275849044
      // windowInsets = WindowInsets(0),
      shape = ExpeBottomSheetShape,
      content = {
        bottomSheetContent(bottomSheetState().bottomSheetState, hideBottomSheet)
      },
    )
  }
}