package com.emendo.expensestracker.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.accounts.AccountsNavGraph
import com.emendo.categories.CategoriesNavGraph
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.transactions.TransactionsNavGraph
import com.ramcosta.composedestinations.spec.NavGraphSpec

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
  @StringRes val titleTextId: Int,
) {
  ACCOUNTS(
    screen = AccountsNavGraph,
    selectedIcon = ExpIcons.CreditCard,
    unselectedIcon = ExpIcons.CreditCardBorder,
    iconTextId = R.string.accounts,
    titleTextId = R.string.accounts,
  ),
  CATEGORIES(
    screen = CategoriesNavGraph,
    selectedIcon = ExpIcons.DonutLarge,
    unselectedIcon = ExpIcons.DonutLargeBorder,
    iconTextId = R.string.categories,
    titleTextId = R.string.categories,
  ),
  TRANSACTIONS(
    screen = TransactionsNavGraph,
    selectedIcon = ExpIcons.ReceiptLong,
    unselectedIcon = ExpIcons.ReceiptLongBorder,
    iconTextId = R.string.transactions,
    titleTextId = R.string.transactions,
  ),
}