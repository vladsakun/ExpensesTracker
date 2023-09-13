package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.ui.bottomsheet.ExpeModalBottomSheet
import de.palm.composestateevents.EventEffect
import de.palm.composestateevents.NavigationEventEffect
import kotlinx.coroutines.launch

@Composable
fun <BSType> BaseScreenWithModalBottomSheetWithViewModel(
  viewModel: BaseBottomSheetViewModel<BSType>,
  onNavigateUpClick: () -> Unit,
  content: @Composable () -> Unit,
  bottomSheetContent: @Composable (bottomSheetType: BSType?, closeBottomSheet: () -> Unit) -> Unit,
) {
  BaseScreenWithModalBottomSheet(
    bottomSheetState = viewModel.bottomSheetState.collectAsStateWithLifecycle(),
    onNavigationClick = onNavigateUpClick,
    onBottomSheetDismissRequest = viewModel::onDismissBottomSheetRequest,
    content = content,
    bottomSheetContent = bottomSheetContent,
    onConsumedNavigateUpEvent = viewModel::onConsumedNavigateUpEvent,
    onConsumedHideBottomSheetEvent = viewModel::onConsumedHideBottomSheetEvent,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <BSType> BaseScreenWithModalBottomSheet(
  bottomSheetState: State<BaseBottomSheetState<BSType?>>,
  onNavigationClick: () -> Unit,
  onBottomSheetDismissRequest: () -> Unit,
  onConsumedNavigateUpEvent: () -> Unit,
  onConsumedHideBottomSheetEvent: () -> Unit,
  bottomSheetContent: @Composable (bottomSheetType: BSType?, closeBottomSheet: () -> Unit) -> Unit,
  content: @Composable () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val closeBottomSheet: () -> Unit = remember(modalBottomSheetState) {
    {
      coroutineScope.launch {
        modalBottomSheetState.hide()
      }
    }
  }
  BackHandler {
    when {
      modalBottomSheetState.isVisible -> closeBottomSheet()
      else -> onNavigationClick()
    }
  }
  Effects(
    bottomSheetState = bottomSheetState,
    onConsumedNavigateUpEvent = onConsumedNavigateUpEvent,
    onNavigationClick = onNavigationClick,
    onConsumedHideBottomSheetEvent = onConsumedHideBottomSheetEvent,
    closeBottomSheet = closeBottomSheet,
    modalBottomSheetState = modalBottomSheetState,
    onBottomSheetDismissRequest = onBottomSheetDismissRequest
  )

  content()

  ExpeModalBottomSheet(
    modalBottomSheetState = modalBottomSheetState,
    bottomSheetState = bottomSheetState,
    closeBottomSheet = closeBottomSheet,
    bottomSheetContent = bottomSheetContent,
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Effects(
  bottomSheetState: State<BaseBottomSheetState<*>>,
  onConsumedNavigateUpEvent: () -> Unit,
  onNavigationClick: () -> Unit,
  onConsumedHideBottomSheetEvent: () -> Unit,
  closeBottomSheet: () -> Unit,
  modalBottomSheetState: SheetState,
  onBottomSheetDismissRequest: () -> Unit,
) {
  NavigationEventEffect(
    event = bottomSheetState.value.navigateUpEvent,
    onConsumed = onConsumedNavigateUpEvent,
    action = onNavigationClick,
  )
  EventEffect(
    event = bottomSheetState.value.hideBottomSheetEvent,
    onConsumed = onConsumedHideBottomSheetEvent,
    action = closeBottomSheet,
  )
  LaunchedEffect(Unit) {
    snapshotFlow { modalBottomSheetState.currentValue }
      .collect {
        if (it == SheetValue.Hidden) onBottomSheetDismissRequest()
      }
  }
}