package com.emendo.expensestracker.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.accounts.AccountsNavGraph
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.CategoriesNavGraph
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.createtransaction.CreatetransactionNavGraph
import com.emendo.expensestracker.report.ReportNavGraph
import com.emendo.expensestracker.transactions.TransactionsNavGraph
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.ramcosta.composedestinations.utils.startDestination

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
  val screen: NavGraphSpec,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  @StringRes val iconTextId: Int,
  @StringRes val titleTextId: Int?,
) {
  ACCOUNTS(
    screen = AccountsNavGraph,
    selectedIcon = ExpeIcons.CreditCard,
    unselectedIcon = ExpeIcons.CreditCardBorder,
    iconTextId = R.string.accounts,
    titleTextId = R.string.accounts,
  ),
  CATEGORIES(
    screen = CategoriesNavGraph,
    selectedIcon = ExpeIcons.DonutLarge,
    unselectedIcon = ExpeIcons.DonutLargeBorder,
    iconTextId = R.string.categories,
    titleTextId = R.string.categories,
  ),
  CREATE_TRANSACTION(
    screen = CreatetransactionNavGraph,
    selectedIcon = ExpeIcons.AddCircle,
    unselectedIcon = ExpeIcons.AddCircle,
    iconTextId = R.string.add,
    titleTextId = null,
  ),
  TRANSACTIONS(
    screen = TransactionsNavGraph,
    selectedIcon = ExpeIcons.ReceiptLong,
    unselectedIcon = ExpeIcons.ReceiptLongBorder,
    iconTextId = R.string.transactions,
    titleTextId = R.string.transactions,
  ),
  REPORT(
    screen = ReportNavGraph,
    selectedIcon = ExpeIcons.Analytics,
    unselectedIcon = ExpeIcons.Analytics,
    iconTextId = R.string.report_title,
    titleTextId = R.string.report_title,
  ),
  ;

  companion object {
    internal val startDestination: TopLevelDestination
      get() = TopLevelDestination.entries.first { it.screen == NavGraphs.root.startRoute }

    internal val routesWithoutCreateTransaction: Set<String> by lazy(LazyThreadSafetyMode.NONE) {
      entries
        .toMutableList()
        .apply { remove(CREATE_TRANSACTION) }
        .map { it.screen.startRoute.startDestination.route }
        .toSet()
    }
  }
}
