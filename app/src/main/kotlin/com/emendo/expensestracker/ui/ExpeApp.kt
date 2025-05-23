package com.emendo.expensestracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_REPORT
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun ExpeApp(appState: ExpeAppState) {
  Scaffold(
    bottomBar = {
      if (appState.showBottomBar) {
      }

      if (appState.showNavigationBar) {
        val routesOnBackStack = rememberRoutesOnBackStack(appState)
        ExpeBottomBar(
          routesOnBackStack = routesOnBackStack.value,
          appState = appState,
        )
      }
    },
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
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

  LaunchedEffect(Unit) {
    if (IS_DEBUG_REPORT) {
      appState.navigateToTopLevelDestination(TopLevelDestination.REPORT)
    }
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
        },
      )
    }
  }
}

@Composable
private fun rememberRoutesOnBackStack(appState: ExpeAppState): MutableState<ImmutableList<TopLevelDestination>> =
  remember(appState.currentDestination) {
    val routesOnBackStack: MutableList<TopLevelDestination> = appState.topLevelDestination.mapNotNull {
      val isRouteOnBackStack = appState.navController.isRouteOnBackStack(it.screen.startRoute)
      if (isRouteOnBackStack) it else null
    }.toMutableList()
    if (routesOnBackStack.size > 1) {
      routesOnBackStack.remove(TopLevelDestination.startDestination)
    }
    mutableStateOf(routesOnBackStack.toPersistentList())
  }

private fun TopLevelDestination.isSelected(routesOnBackStack: ImmutableList<TopLevelDestination>): Boolean =
  if (routesOnBackStack.contains(TopLevelDestination.CREATE_TRANSACTION)) {
    false
  } else {
    this == routesOnBackStack.firstOrNull()
  }
