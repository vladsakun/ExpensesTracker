package com.emendo.categories.create

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.ui.bottomsheet.ExpeModalBottomSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun <BSType> BaseScreenWithModalBottomSheetWithViewModel(
  viewModelWithBottomSheet: BaseBottomSheetViewModel<BSType>,
  onNavigateUpClick: () -> Unit,
  screenContent: @Composable () -> Unit,
  bottomSheetContent: @Composable (bottomSheetType: BSType?, closeBottomSheet: () -> Unit) -> Unit,
) {
  BaseScreenWithModalBottomSheet(
    bottomSheetType = viewModelWithBottomSheet.bottomSheetState.collectAsStateWithLifecycle(),
    hideBottomSheetEvent = viewModelWithBottomSheet.hideBottomSheetEvent,
    navigateUpEvent = viewModelWithBottomSheet.navigateUpEvent,
    onNavigationClick = onNavigateUpClick,
    onBottomSheetDismissRequest = viewModelWithBottomSheet::onDismissBottomSheetRequest,
    content = screenContent,
    bottomSheetContent = bottomSheetContent,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <BSType> BaseScreenWithModalBottomSheet(
  bottomSheetType: State<BSType?>,
  hideBottomSheetEvent: Flow<Unit>,
  navigateUpEvent: Flow<Unit>,
  onNavigationClick: () -> Unit,
  onBottomSheetDismissRequest: () -> Unit,
  content: @Composable () -> Unit,
  bottomSheetContent: @Composable (bottomSheetType: BSType?, closeBottomSheet: () -> Unit) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val closeBottomSheet: () -> Unit = remember(bottomSheetState) {
    {
      coroutineScope.launch {
        bottomSheetState.hide()
      }
    }
  }
  BackHandler {
    when {
      bottomSheetState.isVisible -> closeBottomSheet()
      else -> onNavigationClick()
    }
  }
  LaunchedEffect(Unit) { hideBottomSheetEvent.collect { closeBottomSheet() } }
  LaunchedEffect(Unit) { navigateUpEvent.collect { onNavigationClick() } }
  LaunchedEffect(Unit) {
    snapshotFlow { bottomSheetState.currentValue }
      .collect {
        if (it == SheetValue.Hidden) onBottomSheetDismissRequest()
      }
  }

  content()

  ExpeModalBottomSheet(
    bottomSheetState = bottomSheetState,
    bottomSheetType = bottomSheetType,
    bottomSheetContent = bottomSheetContent,
    closeBottomSheet = closeBottomSheet,
  )
}