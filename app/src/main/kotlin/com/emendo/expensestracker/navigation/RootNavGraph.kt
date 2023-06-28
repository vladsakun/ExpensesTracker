package com.emendo.expensestracker.navigation

import androidx.compose.ui.ExperimentalComposeUiApi
import com.emendo.accounts.AccountsNavGraph
import com.emendo.accounts.destinations.AccountsScreenDestination
import com.emendo.categories.CategoriesNavGraph
import com.emendo.categories.destinations.CategoriesScreenDestination
import com.emendo.transactions.TransactionsNavGraph
import com.emendo.transactions.destinations.TransactionsScreenDestination
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

@ExperimentalComposeUiApi
object RootNavGraph : NavGraphSpec {

  override val route = "root"

  override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
    AccountsScreenDestination,
    CategoriesScreenDestination,
    TransactionsScreenDestination,
  ).associateBy { it.route }

  override val startRoute = AccountsNavGraph

  override val nestedNavGraphs = listOf(
    AccountsNavGraph,
    CategoriesNavGraph,
    TransactionsNavGraph,
  )
}