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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBar
import com.emendo.expensestracker.core.designsystem.component.ExpeNavigationBarItem
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.createtransaction.destinations.CreateTransactionScreenDestination
import com.emendo.expensestracker.navigation.ExpeNavHost
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import timber.log.Timber

@OptIn(ExperimentalLayoutApi::class)
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
      if (appState.currentDestinationSpec.isTopLevelDestination()) {
      }
      ExpeBottomBar(
        destinations = appState.topLevelDestination,
        onNavigateToDestination = appState::navigateToTopLevelDestination,
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
  destinations: List<TopLevelDestination>,
  onNavigateToDestination: (TopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier,
  appState: ExpeAppState,
) {
  val isCreateTransactionOnBackStack = appState.navController.isRouteOnBackStack(CreateTransactionScreenDestination)
  ExpeNavigationBar(modifier = modifier) {
    destinations.forEach { item ->
      if (item == TopLevelDestination.CREATE_TRANSACTION) {
        NavigationBarItem(
          onClick = { onNavigateToDestination(item) },
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

      val selected = if (isCreateTransactionOnBackStack) {
        false
      } else {
        currentDestination.isTopLevelDestinationInHierarchy(item)
      }
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

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination): Boolean {
  return this?.hierarchy?.any {
    it.route?.contains(destination.name, true) ?: false
  } ?: false
}

private fun NavDestination?.containsCreateTransactionScreen() =
  this?.hierarchy?.any { it.route == TopLevelDestination.CREATE_TRANSACTION.screen.startRoute.route } ?: false

private fun DestinationSpec<*>?.isTopLevelDestination(): Boolean {
  Timber.d(
    "NavDestinationRoute: %s, screenRoute: %s",
    this?.route,
    TopLevelDestination.CATEGORIES.screen.startRoute.route
  )
  return TopLevelDestination.entries.any { it.screen.startRoute.route == this?.route }
}