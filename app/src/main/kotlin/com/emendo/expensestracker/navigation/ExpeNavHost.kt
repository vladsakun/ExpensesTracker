package com.emendo.expensestracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.emendo.accounts.destinations.AccountsScreenDestination
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_ACCOUNT
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET
import com.emendo.expensestracker.ui.ExpeAppState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.delay

@Composable
fun ExpeNavHost(
  appState: ExpeAppState,
  onShowSnackbar: suspend (String, String?) -> Boolean,
  modifier: Modifier = Modifier,
) {
  val navController = appState.navController

  if (IS_DEBUG_ACCOUNT) {
    LaunchedEffect(key1 = Unit) {
      delay(200)
      navController.navigate(AccountsScreenDestination)
    }
  }

  DestinationsNavHost(
    navController = navController,
    navGraph = NavGraphs.root,
    modifier = modifier,
  )
}