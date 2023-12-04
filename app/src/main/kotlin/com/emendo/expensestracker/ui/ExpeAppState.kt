package com.emendo.expensestracker.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberExpeAppState(
  windowSizeClass: WindowSizeClass,
  navController: NavHostController,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
): ExpeAppState {
  return remember(
    navController,
    coroutineScope,
    windowSizeClass,
  ) {
    ExpeAppState(
      navController,
      coroutineScope,
      windowSizeClass,
    )
  }
}

@Stable
class ExpeAppState(
  val navController: NavHostController,
  val coroutineScope: CoroutineScope,
  val windowSizeClass: WindowSizeClass,
) {

  val currentDestination: NavDestination?
    @Composable get() = navController.currentBackStackEntryAsState().value?.destination

  val currentDestinationSpec: DestinationSpec<*>?
    @Composable get() = navController.currentDestinationAsState().value

  val shouldShowBottomBar: Boolean
    get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

  val shouldShowNavRail: Boolean
    get() = !shouldShowBottomBar

  /**
   * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
   * route.
   */
  val topLevelDestination: ImmutableList<TopLevelDestination> = TopLevelDestination.entries.toImmutableList()

  /**
   * UI logic for navigating to a top level destination in the app. Top level destinations have
   * only one copy of the destination of the back stack, and save and restore state whenever you
   * navigate to and from it.
   *
   * @param topLevelDestination: The destination the app needs to navigate to.
   */
  fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    if (navController.currentDestination?.parent?.route == topLevelDestination.screen.route) {
      return
    }

    navController.popBackStack(
      route = TopLevelDestination.CREATE_TRANSACTION.screen.route,
      inclusive = true,
    )

    navController.navigate(topLevelDestination.screen) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      if (topLevelDestination != TopLevelDestination.CREATE_TRANSACTION) {
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
        }
      }

      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }
}