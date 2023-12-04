package com.emendo.expensestracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.accounts.create.CreateAccountRoute
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.accounts.destinations.AccountsScreenRouteDestination
import com.emendo.expensestracker.accounts.destinations.CreateAccountRouteDestination
import com.emendo.expensestracker.accounts.detail.AccountDetailScreen
import com.emendo.expensestracker.categories.create.CreateCategoryRoute
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectCurrencyScreenDestination
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_ACCOUNT
import com.emendo.expensestracker.ui.ExpeAppState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.resultRecipient
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
      navController.navigate(AccountsScreenRouteDestination)
    }
  }

  DestinationsNavHost(
    navController = appState.navController,
    navGraph = NavGraphs.root,
    modifier = modifier,
  ) {
    composable(CreateAccountRouteDestination) {
      CreateAccountRoute(
        navigator = destinationsNavigator,
        colorResultRecipient = resultRecipient<SelectColorScreenDestination, Int>(),
        currencyResultRecipient = resultRecipient<SelectCurrencyScreenDestination, String>(),
      )
    }
    composable(AccountDetailScreenDestination) {
      AccountDetailScreen(
        navigator = destinationsNavigator,
        accountId = navArgs.accountId,
        colorResultRecipient = resultRecipient<SelectColorScreenDestination, Int>(),
        currencyResultRecipient = resultRecipient<SelectCurrencyScreenDestination, String>(),
      )
    }
    composable(CreateCategoryRouteDestination) {
      CreateCategoryRoute(
        navigator = destinationsNavigator,
        categoryType = navArgs.categoryType,
        colorResultRecipient = resultRecipient<SelectColorScreenDestination, Int>(),
      )
    }
  }
}