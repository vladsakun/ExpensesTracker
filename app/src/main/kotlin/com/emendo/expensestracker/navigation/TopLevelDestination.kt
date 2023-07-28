package com.emendo.expensestracker.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.accounts.destinations.AccountsScreenDestination
import com.emendo.categories.destinations.CategoriesListScreenDestination
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.transactions.destinations.TransactionsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.emendo.expensestracker.feature.accounts.R as AR
import com.emendo.expensestracker.feature.categories.R as CR
import com.emendo.expensestracker.feature.transactions.R as TR

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
  val direction: DirectionDestinationSpec,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  @StringRes val iconTextId: Int,
  @StringRes val titleTextId: Int,
) {
  ACCOUNTS(
    direction = AccountsScreenDestination,
    selectedIcon = ExpIcons.Accounts,
    unselectedIcon = ExpIcons.AccountsBorder,
    iconTextId = AR.string.accounts,
    titleTextId = AR.string.accounts,
  ),
  CATEGORIES(
    direction = CategoriesListScreenDestination,
    selectedIcon = ExpIcons.DonutLarge,
    unselectedIcon = ExpIcons.DonutLargeBorder,
    iconTextId = CR.string.categories,
    titleTextId = CR.string.categories,
  ),
  TRANSACTIONS(
    direction = TransactionsScreenDestination,
    selectedIcon = ExpIcons.ReceiptLong,
    unselectedIcon = ExpIcons.ReceiptLongBorder,
    iconTextId = TR.string.transactions,
    titleTextId = TR.string.transactions,
  ),
}