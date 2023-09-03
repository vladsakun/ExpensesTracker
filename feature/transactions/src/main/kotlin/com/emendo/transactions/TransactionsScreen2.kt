package com.emendo.transactions

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.Flow

interface GeneralBottomSheetActions {
  fun onPositiveClick(bottomSheetType: Int)
  fun onNegativeClick(bottomSheetType: Int)
  fun onNeutralClick(bottomSheetType: Int)
}

@Destination
@Composable
fun TransactionsScreen2(
  viewModel: TransactionsScreen2ViewModel = hiltViewModel(),
) {
  val bottomSheetType = viewModel.bottomSheetState.collectAsStateWithLifecycle()

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Button(onClick = viewModel::onLogoutClick) {
      Text(text = "Show Log Out Bottom Sheet")
    }
  }

  BottomSheet(
    bottomSheetType = bottomSheetType,
    hideBottomSheetFlow = viewModel.hideBottomSheetFlow,
    onDismissBottomSheet = viewModel::onDismissBottomSheet,
    onBottomSheetHidden = viewModel::onBottomSheetHidden,
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheet(
  bottomSheetType: State<TransactionsScreen2BottomSheets?>,
  hideBottomSheetFlow: Flow<Unit>,
  onDismissBottomSheet: () -> Unit,
  onBottomSheetHidden: () -> Unit,
) {
  val shouldShowBottomSheet = remember { derivedStateOf { bottomSheetType.value != null } }
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  LaunchedEffect(hideBottomSheetFlow) {
    hideBottomSheetFlow.collect {
      bottomSheetState.hide()
      onBottomSheetHidden()
    }
  }

  BottomSheetState(
    shouldShowBottomSheet.value,
    bottomSheetType,
    bottomSheetState,
    onDismissBottomSheet
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheetState(
  shouldShowBottomSheet: Boolean,
  bottomSheetType: State<TransactionsScreen2BottomSheets?>,
  bottomSheetState: SheetState,
  onDismissRequest: () -> Unit,
) {
  if (shouldShowBottomSheet) {
    ModalBottomSheet(
      onDismissRequest = onDismissRequest,
      sheetState = bottomSheetState,
      windowInsets = WindowInsets(0),
    ) {
      BottomSheet(bottomSheetType)
      Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
    }
  }
}

@Composable
private fun BottomSheet(bottomSheetType: State<TransactionsScreen2BottomSheets?>) {
  AnimatedContent(
    targetState = bottomSheetType.value,
    transitionSpec = {
      fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
    },
    label = "BS Content",
  ) { targetState ->
    when (targetState) {
      is TransactionsScreen2BottomSheets.LogOutBottomSheet ->
        GeneralBottomSheet(
          actions = targetState.actions,
          bottomSheetType = 1,
          positiveButtonText = "Log Out",
          negativeButtonText = "Delete Account"
        )

      is TransactionsScreen2BottomSheets.ConfirmDeleteAccountBottomSheet ->
        GeneralBottomSheet(
          actions = targetState.actions,
          bottomSheetType = 2,
          positiveButtonText = "Cancel",
          negativeButtonText = "Confirm Delete",
          neutralButtonText = "Neutral button",
        )

      else -> Unit
    }
  }
}

@Composable
private fun GeneralBottomSheet(
  actions: GeneralBottomSheetActions,
  bottomSheetType: Int,
  positiveButtonText: String,
  negativeButtonText: String,
  neutralButtonText: String? = null,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = Dimens.margin_large_x),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Button(onClick = { actions.onPositiveClick(bottomSheetType) }) {
      Text(text = positiveButtonText)
    }
    Button(onClick = { actions.onNegativeClick(bottomSheetType) }) {
      Text(text = negativeButtonText)
    }
    neutralButtonText?.let {
      Button(onClick = { actions.onNegativeClick(bottomSheetType) }) {
        Text(text = neutralButtonText)
      }
    }
  }
}

sealed interface TransactionsScreen2BottomSheets {
  data class LogOutBottomSheet(val actions: GeneralBottomSheetActions) : TransactionsScreen2BottomSheets
  data class ConfirmDeleteAccountBottomSheet(val actions: GeneralBottomSheetActions) : TransactionsScreen2BottomSheets
}

