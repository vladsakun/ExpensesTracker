package com.emendo.expensestracker.navigation

import com.emendo.expensestracker.accounts.AccountsNavGraph
import com.emendo.expensestracker.categories.CategoriesNavGraph
import com.emendo.expensestracker.core.app.base.shared.AppbaseuiNavGraph
import com.emendo.expensestracker.createtransaction.CreatetransactionNavGraph
import com.emendo.expensestracker.transactions.TransactionsNavGraph
import com.emendo.expensestracker.usersettings.UsersettingsNavGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

object NavGraphs {
    val root =
        object : NavGraphSpec {
            override val route: String = "root"
            override val startRoute = getStartDestination()
            override val destinationsByRoute: Map<String, DestinationSpec<*>> = emptyMap()
            override val nestedNavGraphs =
                listOf(
                    CategoriesNavGraph,
                    AccountsNavGraph,
                    TransactionsNavGraph,
                    UsersettingsNavGraph,
                    CreatetransactionNavGraph,
                    AppbaseuiNavGraph,
                )
        }

    private fun getStartDestination(): NavGraphSpec = CategoriesNavGraph
}
