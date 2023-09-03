package com.emendo.expensestracker.navigation

import com.emendo.accounts.AccountsNavGraph
import com.emendo.categories.CategoriesNavGraph
import com.emendo.transactions.TransactionsNavGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

object NavGraphs {

  val root = object : NavGraphSpec {
    override val route: String = "root"
    override val startRoute = getStartDestination()
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = emptyMap()
    override val nestedNavGraphs = listOf(
      CategoriesNavGraph,
      AccountsNavGraph,
      TransactionsNavGraph,
    )
  }

  private fun getStartDestination(): NavGraphSpec = CategoriesNavGraph
}