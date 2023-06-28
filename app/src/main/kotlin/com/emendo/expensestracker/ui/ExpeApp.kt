package com.emendo.expensestracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination
import component.ExpeNavigationBar
import component.ExpeNavigationBarItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpeApp(
  windowSizeClass: WindowSizeClass,
  appState: ExpeAppState = rememberExpeAppState(
    windowSizeClass = windowSizeClass
  )
) {
  Scaffold(
    containerColor = Color.Transparent,
    contentColor = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      if (appState.shouldShowBottomBar) {
        ExpeBottomBar(
          destinations = appState.topLevelDestination,
          onNavigateToDestination = appState::navigateToTopLevelDestination,
          currentDestination = appState.currentDestination,
        )
      }
    }
  ) { padding ->
    Row(
      Modifier
        .fillMaxSize()
        .padding(padding)
        .consumeWindowInsets(padding)
        .windowInsetsPadding(
          WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal,
          ),
        ),
    ) {
      Column(modifier = Modifier.fillMaxSize()) {

        ExpeNavHost(appState = appState, onShowSnackbar = { message, action ->
          return@ExpeNavHost true
        })
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
  ExpeNavigationBar(
    modifier = modifier,
  ) {
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