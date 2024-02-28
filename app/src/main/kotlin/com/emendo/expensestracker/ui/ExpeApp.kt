package com.emendo.expensestracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.ramcosta.composedestinations.utils.isRouteOnBackStack

@Composable
fun ExpeApp(
  windowSizeClass: WindowSizeClass,
  navController: NavHostController,
  appState: ExpeAppState = rememberExpeAppState(
    windowSizeClass = windowSizeClass,
    navController = navController,
  ),
) {
  ExpeScaffold(
    bottomBar = {
      if (appState.shouldShowBottomBar) {
      }
      ExpeBottomBar(
        currentDestination = appState.currentDestination,
        appState = appState,
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

@Composable
private fun ExpeBottomBar(
  // Used to be recomposed on each navigation
  @Suppress("UNUSED_PARAMETER")
  currentDestination: NavDestination?,
  appState: ExpeAppState,
  modifier: Modifier = Modifier,
) {
  val routesOnBackStack: MutableList<TopLevelDestination> =
    appState.topLevelDestination
      .mapNotNull {
        val isRouteOnBackStack = appState.navController.isRouteOnBackStack(it.screen.startRoute)
        if (isRouteOnBackStack) it else null
      }
      .toMutableList()

  if (routesOnBackStack.size > 1) {
    routesOnBackStack.remove(TopLevelDestination.start)
  }

  ExpeNavigationBar(modifier = modifier) {
    appState.topLevelDestination.forEach { item ->
      if (item == TopLevelDestination.CREATE_TRANSACTION) {
        NavigationBarItem(
          onClick = { appState.navigateToTopLevelDestination(item) },
          selected = false,
          icon = {
            Icon(
              imageVector = ExpeIcons.AddCircle,
              contentDescription = "add",
              modifier = Modifier.size(Dimens.icon_button_size),
              tint = MaterialTheme.colorScheme.primary,
            )
          },
        )

        return@forEach
      }

      ExpeNavigationBarItem(
        selected = item.isSelected(routesOnBackStack),
        onClick = { appState.navigateToTopLevelDestination(item) },
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
          item.titleTextId?.let { title ->
            Text(
              text = stringResource(title),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.Center,
            )
          }
        }
      )
    }
  }
}

private fun TopLevelDestination.isSelected(routesOnBackStack: MutableList<TopLevelDestination>): Boolean =
  if (routesOnBackStack.contains(TopLevelDestination.CREATE_TRANSACTION)) {
    false
  } else {
    this == routesOnBackStack.firstOrNull()
  }