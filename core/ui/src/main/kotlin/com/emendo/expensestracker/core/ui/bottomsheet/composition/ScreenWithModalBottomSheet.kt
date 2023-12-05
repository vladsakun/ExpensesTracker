package com.emendo.expensestracker.core.ui.bottomsheet.composition

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.ui.bottomsheet.ExpeModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetState
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.NavigationEventEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <BSType> ScreenWithModalBottomSheet(
  stateManager: BottomSheetStateManager<BSType>,
  onNavigateUpClick: () -> Unit,
  bottomSheetContent: @Composable (bottomSheetType: BSType, hideBottomSheet: () -> Unit) -> Unit,
  content: @Composable () -> Unit,
) {
  val bottomSheetState = stateManager.bottomSheetState.collectAsStateWithLifecycle()

  BaseScreenWithModalBottomSheet(
    bottomSheetState = bottomSheetState::value,
    onNavigationClick = onNavigateUpClick,
    onBottomSheetDismissRequest = stateManager::dismissBottomSheet,
    onConsumedNavigateUpEvent = stateManager::consumeNavigateUpEvent,
    onConsumedHideBottomSheetEvent = stateManager::onConsumedHideBottomSheetEvent,
    confirmValueChange = stateManager::confirmValueChange,
    bottomSheetContent = bottomSheetContent,
    content = content,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <BSType> BaseScreenWithModalBottomSheet(
  bottomSheetState: () -> BottomSheetState<BSType?>,
  onNavigationClick: () -> Unit,
  onBottomSheetDismissRequest: () -> Unit,
  onConsumedNavigateUpEvent: () -> Unit,
  onConsumedHideBottomSheetEvent: () -> Unit,
  confirmValueChange: (SheetValue) -> Boolean,
  bottomSheetContent: @Composable (bottomSheetType: BSType, hideBottomSheet: () -> Unit) -> Unit,
  content: @Composable () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val modalBottomSheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = true,
    confirmValueChange = confirmValueChange
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
    hideBottomSheet = hideBottomSheet,
    bottomSheetContent = bottomSheetContent,
    onDismissRequest = onBottomSheetDismissRequest,
  )
}

@Composable
private fun Effects(
  bottomSheetStateProvider: () -> BottomSheetState<*>,
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