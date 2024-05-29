package com.emendo.expensestracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun ExpeApp(
  windowSizeClass: WindowSizeClass,
  navController: NavHostController,
  appState: ExpeAppState =
      rememberExpeAppState(
          windowSizeClass = windowSizeClass,
          navController = navController,
      ),
) {
  ExpeScaffold(
    bottomBar = {
      if (appState.shouldShowBottomBar) {
      }

        val routesOnBackStack = rememberRoutesOnBackStack(appState)
      ExpeBottomBar(
          routesOnBackStack = routesOnBackStack.value,
        appState = appState,
      )
    },
  ) { padding ->
    Row(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(padding)
            .consumeWindowInsets(padding)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
      ExpeNavHost(
        appState = appState,
        onShowSnackbar = { message, action ->
          return@ExpeNavHost true
        },
      )
    }
  }
}

@Composable
private fun ExpeBottomBar(
    routesOnBackStack: ImmutableList<TopLevelDestination>,
  appState: ExpeAppState,
  modifier: Modifier = Modifier,
) {
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
        },
      )
    }
  }
}

@Composable
private fun rememberRoutesOnBackStack(appState: ExpeAppState): MutableState<ImmutableList<TopLevelDestination>> =
    remember(appState.currentDestination) {
        val routesOnBackStack: MutableList<TopLevelDestination> =
            appState.topLevelDestination
                .mapNotNull {
                    val isRouteOnBackStack =
                        appState.navController.isRouteOnBackStack(it.screen.startRoute)
                    if (isRouteOnBackStack) it else null
                }
                .toMutableList()
        if (routesOnBackStack.size > 1) {
            routesOnBackStack.remove(TopLevelDestination.start)
        }
        mutableStateOf(routesOnBackStack.toPersistentList())
    }

private fun TopLevelDestination.isSelected(routesOnBackStack: ImmutableList<TopLevelDestination>): Boolean =
  if (routesOnBackStack.contains(TopLevelDestination.CREATE_TRANSACTION)) {
    false
  } else {
    this == routesOnBackStack.firstOrNull()
  }
