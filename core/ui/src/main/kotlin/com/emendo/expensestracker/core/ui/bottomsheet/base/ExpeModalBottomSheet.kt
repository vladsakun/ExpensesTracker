package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.designsystem.utils.ExpeBottomSheetShape
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
internal fun ExpeModelBottomSheet(
  modalBottomSheetState: SheetState,
  onDismissRequest: () -> Unit,
  bottomSheetContent: @Composable (ColumnScope.(type: BottomSheetData) -> Unit),
  bottomSheetState: () -> BottomSheetState,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    StatusBarScrim(modalBottomSheetState)
    ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = modalBottomSheetState,
      // Todo uncomment when fixed https://issuetracker.google.com/issues/275849044
      // windowInsets = WindowInsets(0),
      windowInsets = WindowInsets.statusBars,
      shape = ExpeBottomSheetShape,
      tonalElevation = 0.dp,
      content = {
        Column(
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBarsIgnoringVisibility),
        ) {
          bottomSheetContent(bottomSheetState().bottomSheetData!!)
        }
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.StatusBarScrim(modalBottomSheetState: SheetState) {
  val scrimAlpha by animateFloatAsState(
    targetValue = if (modalBottomSheetState.targetValue != SheetValue.Hidden) 1f else 0f,
    animationSpec = TweenSpec(),
    label = "StatusBarScrimAlpha",
  )
  val scrimColor = BottomSheetDefaults.ScrimColor

  Canvas(
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsTopHeight(WindowInsets.statusBars)
      .align(Alignment.TopCenter),
  ) {
    drawRect(color = scrimColor, alpha = scrimAlpha)
  }
}