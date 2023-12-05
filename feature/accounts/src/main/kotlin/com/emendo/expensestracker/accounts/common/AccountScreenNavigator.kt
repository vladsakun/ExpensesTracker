package com.emendo.expensestracker.accounts.common

interface AccountScreenNavigator {
  fun openSelectIconScreen(preselectedIconId: Int)
  fun openSelectColorScreen(preselectedColorId: Int)
  fun openSelectCurrencyScreen()
}