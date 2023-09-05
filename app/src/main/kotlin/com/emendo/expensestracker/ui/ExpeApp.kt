package com.emendo.expensestracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.utils.ExpeBottomSheetShape
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpeApp(
  windowSizeClass: WindowSizeClass,
  appState: ExpeAppState = rememberExpeAppState(windowSizeClass = windowSizeClass),
) {
  val scaffoldState = rememberBottomSheetScaffoldState()

  BottomSheetScaffold(
    modifier = Modifier.fillMaxSize(),
    scaffoldState = scaffoldState,
    sheetContent = {},
    sheetShape = ExpeBottomSheetShape,
    sheetPeekHeight = 0.dp,
  ) {
    ExpeScaffold(
      bottomBar = {
        //        if (appState.shouldShowBottomBar) {
        //
        //        }
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
      }
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