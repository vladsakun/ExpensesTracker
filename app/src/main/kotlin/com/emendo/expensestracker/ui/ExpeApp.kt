package com.emendo.expensestracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.emendo.expensestracker.AppStateCommander
import com.emendo.expensestracker.MainActivityUiState
import com.emendo.expensestracker.core.designsystem.component.ExpeAlertDialog
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.ui.dialog.LoadingDialog
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpeApp(
  windowSizeClass: WindowSizeClass,
  appUiState: () -> MainActivityUiState?,
  appStateCommander: AppStateCommander,
  appState: ExpeAppState = rememberExpeAppState(windowSizeClass = windowSizeClass),
) {
  ExpeScaffold(
    bottomBar = {
      if (appState.shouldShowBottomBar) {
      }
      ExpeBottomBar(
        destinations = appState.topLevelDestination,
        onNavigateToDestination = appState::navigateToTopLevelDestination,
        currentDestination = appState.currentDestination,
      )
    },
  ) { padding ->
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .consumeWindowInsets(padding)
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
      ExpeNavHost(
        appState = appState,
        onShowSnackbar = { message, action ->
          return@ExpeNavHost true
        }
      )

      when (val state = appUiState()) {
        is MainActivityUiState.Loading -> LoadingDialog()
        is MainActivityUiState.ErrorDialog -> ErrorDialog(state, appStateCommander)
        else -> {}
      }
    }
  }
}

@Composable
fun ErrorDialog(
  state: MainActivityUiState.ErrorDialog,
  commander: AppStateCommander,
) {
  ExpeAlertDialog(
    onAlertDialogDismissRequest = commander::onAlertDialogDismissRequest,
    onCloseClick = commander::onNegativeActionClick,
    onConfirmClick = commander::onPositiveActionClick,
    title = state.error.title,
    confirmActionText = state.error.positiveAction.text,
    dismissActionText = state.error.negativeAction.text,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = Dimens.margin_large_xxx)
    ) {
      Text(text = state.error.message)
    }
  }
}

@Composable
private fun ExpeBottomBar(
  destinations: List<TopLevelDestination>,
  onNavigateToDestination: (TopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier,
) {
  ExpeNavigationBar(modifier = modifier) {
    destinations.forEach { item ->
      val selected = currentDestination.isTopLevelDestinationInHierarchy(item)
      ExpeNavigationBarItem(
        selected = selected,
        onClick = { onNavigateToDestination(item) },
        icon = {
          Icon(
            imageVector = item.unselectedIcon,
            contentDescription = null,
          )
        },
        selectedIcon = {
          Icon(
            imageVector = item.selectedIcon,
            contentDescription = null,
          )
        },
        label = {
          Text(
            text = stringResource(item.iconTextId),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
          )
        }
      )
    }
  }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination): Boolean {
  return this?.hierarchy?.any {
    it.route?.contains(destination.name, true) ?: false
  } ?: false
}