package com.emendo.expensestracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.emendo.accounts.destinations.AccountsScreenDestination
import com.emendo.accounts.destinations.CreateAccountRouteDestination
import com.emendo.categories.destinations.CategoriesListScreenDestination
import com.emendo.categories.destinations.CreateCategoryScreenDestination
import com.emendo.expensestracker.ui.ExpeAppState
import com.emendo.transactions.destinations.TransactionsScreen2Destination
import com.emendo.transactions.destinations.TransactionsScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

object NavGraphs {

  val categories = object : NavGraphSpec {
    override val route: String = "categories"
    override val startRoute = CategoriesListScreenDestination
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
      CategoriesListScreenDestination,
      CreateCategoryScreenDestination,
    ).associateBy { it.route }
  }

  val accounts = object : NavGraphSpec {
    override val route: String = "accounts"
    override val startRoute = AccountsScreenDestination
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
      AccountsScreenDestination,
      CreateAccountRouteDestination,
    ).associateBy { it.route }
  }

  val transactions = object : NavGraphSpec {
    override val route: String = "transactions"
    override val startRoute = TransactionsScreenDestination
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
      TransactionsScreenDestination,
      TransactionsScreen2Destination,
    ).associateBy { it.route }
  }

  val root = object : NavGraphSpec {
    override val route: String = "root"
    override val startRoute = categories
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = emptyMap()
    override val nestedNavGraphs = listOf(categories, accounts, transactions)
  }

  private fun getStartDestination(): NavGraphSpec = categories
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpeNavHost(
  appState: ExpeAppState,
  onShowSnackbar: suspend (String, String?) -> Boolean,
  modifier: Modifier = Modifier,
) {
  val navController = appState.navController

  DestinationsNavHost(
    navController = navController,
    navGraph = NavGraphs.root,
    modifier = modifier,
  )
}