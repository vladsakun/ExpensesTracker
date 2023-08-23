package com.emendo.expensestracker.ui

import android.util.Log
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
import androidx.navigation.compose.rememberNavController
import com.emendo.accounts.destinations.AccountsScreenDestination
import com.emendo.categories.destinations.CategoriesListScreenDestination
import com.emendo.expensestracker.navigation.TopLevelDestination
import com.emendo.transactions.destinations.TransactionsScreenDestination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import kotlinx.coroutines.CoroutineScope

private const val TAG = "ExpeAppState"

@Composable
fun rememberExpeAppState(
  windowSizeClass: WindowSizeClass,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
  navController: NavHostController = rememberNavController(),
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
class ExpeAppState constructor(
  val navController: NavHostController,
  val coroutineScope: CoroutineScope,
  val windowSizeClass: WindowSizeClass,
) {

  val currentDestination: NavDestination?
    @Composable get() = navController
      .currentBackStackEntryAsState().value?.destination

  val currentComposableDestination: DestinationSpec<*>?
    @Composable get() = navController.currentDestinationAsState().value

  val currentTopLevelDestination: TopLevelDestination?
    @Composable get() = when (navController.currentDestinationAsState().value) {
      is AccountsScreenDestination -> TopLevelDestination.ACCOUNTS
      is CategoriesListScreenDestination -> TopLevelDestination.CATEGORIES
      is TransactionsScreenDestination -> TopLevelDestination.TRANSACTIONS
      else -> null
    }

  val shouldShowBottomBar: Boolean
    get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

  val shouldShowNavRail: Boolean
    get() = !shouldShowBottomBar

  /**
   * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
   * route.
   */
  val topLevelDestination: List<TopLevelDestination> = TopLevelDestination.values().asList()

  /**
   * UI logic for navigating to a top level destination in the app. Top level destinations have
   * only one copy of the destination of the back stack, and save and restore state whenever you
   * navigate to and from it.
   *
   * @param topLevelDestination: The destination the app needs to navigate to.
   */
  fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
//    val isCurrentDestOnBackStack = navController.isRouteOnBackStack(topLevelDestination.screen)

//    if (isCurrentDestOnBackStack) {
//      // When we click again on a bottom bar item and it was already selected
//      // we want to pop the back stack until the initial destination of this bottom bar item
//      navController.popBackStack(topLevelDestination.direction, inclusive = false)
//      return
//    }

    navController.navigate(topLevelDestination.screen) {
      launchSingleTop = true
      restoreState = true

      popUpTo(navController.graph.findStartDestination().id){
        saveState = true
      }
//      // Pop up to the start destination of the graph to
//      // avoid building up a large stack of destinations
//      // on the back stack as users select items
//      popUpTo(RootNavGraph) {
//        saveState = true
//      }
//
//      // Avoid multiple copies of the same destination when
//      // reselecting the same item
//      launchSingleTop = true
//      // Restore state when reselecting a previously selected item
//      restoreState = true
    }

    val value = navController.currentBackStack.value
    Log.d(TAG, "size: ${value.size} navControllerBackStack: $value")
  }
}