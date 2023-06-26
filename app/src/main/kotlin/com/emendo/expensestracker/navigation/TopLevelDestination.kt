package com.emendo.expensestracker.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.R
import icon.ExpIcons
import com.emendo.expensestracker.feature.accounts.R as AR
import com.emendo.expensestracker.feature.categories.R as CR
import com.emendo.expensestracker.feature.transactions.R as TR

enum class TopLevelDestination(
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  @StringRes val iconTextId: Int,
  @StringRes val titleTextId: Int,
) {
  ACCOUNTS(
    selectedIcon = ExpIcons.Accounts,
    unselectedIcon = ExpIcons.AccountsBorder,
    iconTextId = AR.string.account,
    titleTextId = R.string.app_name,
  ),
  CATEGORIES(
    selectedIcon = ExpIcons.DonutLarge,
    unselectedIcon = ExpIcons.DonutLargeBorder,
    iconTextId = CR.string.categories,
    titleTextId = CR.string.categories,
  ),
  TRANSACTIONS(
    selectedIcon = ExpIcons.ReceiptLong,
    unselectedIcon = ExpIcons.ReceiptLongBorder,
    iconTextId = TR.string.transactions,
    titleTextId = TR.string.transactions,
  ),

}