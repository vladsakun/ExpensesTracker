package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.designsystem.utils.ExpeBottomSheetShape
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.NavigationEventEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithModalBottomSheet(
  stateManager: ModalBottomSheetStateManager,
  onNavigateUpClick: () -> Unit,
  bottomSheetContent: @Composable ColumnScope.(bottomSheetType: BottomSheetData) -> Unit,
  content: @Composable () -> Unit,
) {
  val bottomSheetState = stateManager.modalBottomSheetState.collectAsStateWithLifecycle()

  BaseScreenWithModalBottomSheet(
    bottomSheetState = bottomSheetState::value,
    onNavigationClick = onNavigateUpClick,
    onBottomSheetDismissRequest = stateManager::dismissModalBottomSheet,
    onConsumedNavigateUpEvent = stateManager::consumeNavigateUpEvent,
    onConsumedHideBottomSheetEvent = stateManager::onConsumedHideModalBottomSheetEvent,
    confirmValueChange = stateManager::confirmValueChange,
    bottomSheetContent = bottomSheetContent,
    content = content,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseScreenWithModalBottomSheet(
  bottomSheetState: () -> BottomSheetState,
  onNavigationClick: () -> Unit,
  onBottomSheetDismissRequest: () -> Unit,
  onConsumedNavigateUpEvent: () -> Unit,
  onConsumedHideBottomSheetEvent: () -> Unit,
  confirmValueChange: (SheetValue) -> Boolean,
  bottomSheetContent: @Composable ColumnScope.(bottomSheetType: BottomSheetData) -> Unit,
  content: @Composable () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val modalBottomSheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = true,
    confirmValueChange = confirmValueChange,
  )
  val hideBottomSheet: () -> Unit = remember(modalBottomSheetState) {
    {
      coroutineScope.launch {
        modalBottomSheetState.hide()
        onBottomSheetDismissRequest()
      }
    }
  }
  BackHandler {
    when {
      modalBottomSheetState.isVisible -> hideBottomSheet()
      else -> onNavigationClick()
    }
  }
  Effects(
    bottomSheetStateProvider = bottomSheetState,
    onConsumedNavigateUpEvent = onConsumedNavigateUpEvent,
    onNavigationClick = onNavigationClick,
    onConsumedHideBottomSheetEvent = onConsumedHideBottomSheetEvent,
    hideBottomSheet = hideBottomSheet,
  )

  content()

  ExpeModalBottomSheet(
    modalBottomSheetState = modalBottomSheetState,
    bottomSheetState = bottomSheetState,
    onDismissRequest = onBottomSheetDismissRequest,
    bottomSheetContent = bottomSheetContent,
  )
}

@Composable
private fun Effects(
  bottomSheetStateProvider: () -> BottomSheetState,
  onConsumedNavigateUpEvent: () -> Unit,
  onNavigationClick: () -> Unit,
  onConsumedHideBottomSheetEvent: () -> Unit,
  hideBottomSheet: () -> Unit,
) {
  NavigationEventEffect(
    event = bottomSheetStateProvider().navigateUpEvent,
    onConsumed = onConsumedNavigateUpEvent,
    action = onNavigationClick,
  )
  EventEffect(
    event = bottomSheetStateProvider().hideBottomSheetEvent,
    onConsumed = onConsumedHideBottomSheetEvent,
    action = hideBottomSheet,
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExpeModalBottomSheet(
  modalBottomSheetState: SheetState,
  bottomSheetState: () -> BottomSheetState,
  onDismissRequest: () -> Unit,
  bottomSheetContent: @Composable (ColumnScope.(type: BottomSheetData) -> Unit),
) {
  val shouldOpenBottomSheet = remember { derivedStateOf { bottomSheetState().bottomSheetData != null } }
  val focusManager = LocalFocusManager.current
  val navBarInsets = WindowInsets.navigationBars.asPaddingValues()

  if (shouldOpenBottomSheet.value) {
    focusManager.clearFocus()
    ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = modalBottomSheetState,
      // Todo uncomment when fixed https://issuetracker.google.com/issues/275849044
      // windowInsets = WindowInsets(0),
      windowInsets = WindowInsets.statusBars,
      shape = ExpeBottomSheetShape,
      content = {
        Column(
          modifier = Modifier
            .padding(bottom = navBarInsets.calculateBottomPadding())
        ) {
          bottomSheetContent(bottomSheetState().bottomSheetData!!)
        }
      },
    )
  }
}