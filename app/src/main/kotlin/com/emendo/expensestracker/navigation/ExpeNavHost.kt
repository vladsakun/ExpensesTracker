package com.emendo.expensestracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.accounts.create.CreateAccountRoute
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.accounts.destinations.AccountsScreenRouteDestination
import com.emendo.expensestracker.accounts.destinations.CreateAccountRouteDestination
import com.emendo.expensestracker.accounts.detail.AccountDetailScreen
import com.emendo.expensestracker.accounts.list.selectAccountResultRecipient
import com.emendo.expensestracker.categories.create.CreateCategoryRoute
import com.emendo.expensestracker.categories.destinations.CategoryDetailRouteDestination
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.categories.destinations.CreateSubcategoryRouteDestination
import com.emendo.expensestracker.categories.detail.CategoryDetailRoute
import com.emendo.expensestracker.categories.subcategory.CreateSubcategoryRoute
import com.emendo.expensestracker.categories.subcategory.subcategoryResultRecipient
import com.emendo.expensestracker.core.app.base.shared.color.selectColorResultRecipient
import com.emendo.expensestracker.core.app.base.shared.currency.selectCurrencyResultRecipient
import com.emendo.expensestracker.core.app.base.shared.icon.selectIconResultRecipient
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_ACCOUNT
import com.emendo.expensestracker.createtransaction.destinations.CreateTransactionScreenDestination
import com.emendo.expensestracker.createtransaction.selectcategory.selectCategoryResultRecipient
import com.emendo.expensestracker.createtransaction.transaction.CreateTransactionScreen
import com.emendo.expensestracker.settings.SettingsRoute
import com.emendo.expensestracker.settings.destinations.SettingsRouteDestination
import com.emendo.expensestracker.ui.ExpeAppState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.resultBackNavigator
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
      navController.navigate(AccountsScreenRouteDestination())
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
        colorResultRecipient = selectColorResultRecipient(),
        currencyResultRecipient = selectCurrencyResultRecipient(),
        iconResultRecipient = selectIconResultRecipient(),
      )
    }
    composable(AccountDetailScreenDestination) {
      AccountDetailScreen(
        navigator = destinationsNavigator,
        accountId = navArgs.accountId,
        colorResultRecipient = selectColorResultRecipient(),
        currencyResultRecipient = selectCurrencyResultRecipient(),
        iconResultRecipient = selectIconResultRecipient(),
      )
    }
    composable(CreateCategoryRouteDestination) {
      CreateCategoryRoute(
        navigator = destinationsNavigator,
        categoryType = navArgs.categoryType,
        colorResultRecipient = selectColorResultRecipient(),
        iconResultRecipient = selectIconResultRecipient(),
        subcategoryResultRecipient = subcategoryResultRecipient(),
      )
    }
    composable(CategoryDetailRouteDestination) {
      CategoryDetailRoute(
        navigator = destinationsNavigator,
        categoryId = navArgs.categoryId,
        colorResultRecipient = selectColorResultRecipient(),
        iconResultRecipient = selectIconResultRecipient(),
        subcategoryResultRecipient = subcategoryResultRecipient(),
      )
    }
    composable(CreateTransactionScreenDestination) {
      CreateTransactionScreen(
        navigator = destinationsNavigator,
        accountResultRecipient = selectAccountResultRecipient(),
        categoryResultRecipient = selectCategoryResultRecipient(),
      )
    }
    composable(CreateSubcategoryRouteDestination) {
      CreateSubcategoryRoute(
        navigator = destinationsNavigator,
        iconResultRecipient = selectIconResultRecipient(),
        colorId = navArgs.colorId,
        name = navArgs.name,
        iconId = navArgs.iconId,
        index = navArgs.index,
        resultNavigator = resultBackNavigator(),
      )
    }
    composable(SettingsRouteDestination) {
      SettingsRoute(
        navigator = destinationsNavigator,
        currencyResultRecipient = selectCurrencyResultRecipient(),
      )
    }
  }
}
