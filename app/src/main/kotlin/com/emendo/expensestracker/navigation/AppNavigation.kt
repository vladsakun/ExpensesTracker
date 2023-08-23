package com.emendo.expensestracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emendo.accounts.AccountsNavGraph
import com.emendo.categories.CategoriesNavGraph
import com.emendo.expensestracker.ui.ExpeAppState
import com.emendo.transactions.TransactionsNavGraph
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

object NavGraphs {

  val root = object : NavGraphSpec {
    override val route: String = "root"
    override val startRoute = getStartDestination()
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = emptyMap()
    override val nestedNavGraphs = listOf(CategoriesNavGraph, AccountsNavGraph, TransactionsNavGraph)
  }

  private fun getStartDestination(): NavGraphSpec = CategoriesNavGraph
}

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