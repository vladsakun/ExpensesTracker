package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <BottomSheetType> ExpeModalBottomSheet(
  bottomSheetState: SheetState,
  bottomSheetType: State<BottomSheetType>,
  closeBottomSheet: () -> Unit,
  bottomSheetContent: @Composable (ColumnScope.(type: BottomSheetType, closeBottomSheet: () -> Unit) -> Unit),
) {
  val shouldOpenBottomSheet = remember { derivedStateOf { bottomSheetType.value != null } }
  val focusManager = LocalFocusManager.current

  if (shouldOpenBottomSheet.value) {
    focusManager.clearFocus()
    ModalBottomSheet(
      onDismissRequest = {},
      sheetState = bottomSheetState,
      windowInsets = WindowInsets(0),
      shape = ExpeBottomSheetShape,
      content = { bottomSheetContent(bottomSheetType.value, closeBottomSheet) },
    )
  }
}