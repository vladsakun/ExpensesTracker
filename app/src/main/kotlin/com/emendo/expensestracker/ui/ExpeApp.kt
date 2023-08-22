package com.emendo.expensestracker.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.emendo.accounts.destinations.AccountsScreenDestination
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.utils.ExpeBottomSheetShape
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.ramcosta.composedestinations.navigation.navigate

private const val TAG = "ExpeApp"

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpeApp(
  windowSizeClass: WindowSizeClass,
  appState: ExpeAppState = rememberExpeAppState(windowSizeClass = windowSizeClass),
) {
  val scaffoldState = rememberBottomSheetScaffoldState(SheetState(skipPartiallyExpanded = true))

  BottomSheetScaffold(
    modifier = Modifier.fillMaxSize(),
    scaffoldState = scaffoldState,
    sheetContent = {},
    sheetShape = ExpeBottomSheetShape,
  ) { paddingBottomSheetScaffold ->
    Scaffold(
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      bottomBar = {
        if (appState.shouldShowBottomBar) {
          ExpeBottomBar(
            destinations = appState.topLevelDestination,
            onNavigateToDestination = appState::navigateToTopLevelDestination,
            currentDestination = appState.currentDestination,
          )
        }
      },
    ) { padding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .consumeWindowInsets(padding)
          .windowInsetsPadding(
            WindowInsets.safeDrawing.only(
              WindowInsetsSides.Horizontal,
            ),
          ),
      ) {
        ExpeNavHost(
          appState = appState,
          onShowSnackbar = { message, action ->
            return@ExpeNavHost true
          }
        )
      }
    }
  }
}

@Composable
private fun ExpeBottomBar(
  destinations: List<TopLevelDestination>,
  onNavigateToDestination: (TopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier
) {
  ExpeNavigationBar(modifier = modifier) {
    destinations.forEach { destination ->
      val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
      ExpeNavigationBarItem(
        selected = selected,
        onClick = { onNavigateToDestination(destination) },
        icon = {
          Icon(
            imageVector = destination.unselectedIcon,
            contentDescription = null,
          )
        },
        selectedIcon = {
          Icon(
            imageVector = destination.selectedIcon,
            contentDescription = null,
          )
        },
        label = { Text(text = stringResource(destination.iconTextId)) }
      )
    }
  }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination): Boolean {
  return this?.hierarchy?.any {
    it.route?.contains(destination.name, true) ?: false
  } ?: false
}