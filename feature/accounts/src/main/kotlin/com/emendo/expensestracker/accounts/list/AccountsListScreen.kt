package com.emendo.expensestracker.accounts.list

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.accounts.destinations.CreateAccountRouteDestination
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.component.ExpeTopBar
import com.emendo.expensestracker.core.ui.AccountItem
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@RootNavGraph(start = true)
@Destination
@Composable
fun AccountsScreenRoute(
  navigator: DestinationsNavigator,
  viewModel: AccountsListViewModel = hiltViewModel(),
) {
  if (IS_DEBUG_CREATE_ACCOUNT) {
    LaunchedEffect(Unit) {
      delay(200)
      navigator.navigate(CreateAccountRouteDestination)
    }
  }

  val onBackClick: (() -> Unit)? = if (viewModel.isSelectMode) navigator::navigateUp else null
  val uiState = viewModel.uiState.collectAsStateWithLifecycle()
  AccountsListScreenContent(
    uiStateProvider = uiState::value,
    onAddAccountClick = { navigator.navigate(CreateAccountRouteDestination) },
    onAccountClick = { account ->
      if (viewModel.isSelectMode) {
        viewModel.selectAccountItem(account)
        navigator.navigateUp()
      } else {
        navigator.navigate(AccountDetailScreenDestination(account.id))
      }
    },
    onBackClick = onBackClick,
    titleRestId = viewModel.titleResId,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountsListScreenContent(
  @StringRes titleRestId: Int,
  uiStateProvider: () -> AccountsListUiState,
  onAddAccountClick: () -> Unit,
  onAccountClick: (AccountModel) -> Unit,
  onBackClick: (() -> Unit)?,
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  ExpeScaffold(
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    topBar = {
      ExpeTopBar(
        titleResId = titleRestId,
        scrollBehavior = topAppBarScrollBehavior,
        onNavigationBackClick = onBackClick,
      )
    },
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
      FloatingActionButton(
        onClick = onAddAccountClick,
        content = {
          Icon(
            imageVector = ExpeIcons.Add,
            contentDescription = "Add",
          )
        }
      )
    },
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      when (val state = uiStateProvider()) {
        is AccountsListUiState.Empty -> Unit
        is AccountsListUiState.Loading -> item { ExpLoadingWheel() }
        is AccountsListUiState.Error -> item { Text(text = state.message) }
        is AccountsListUiState.DisplayAccountsList -> {
          items(
            items = state.accountModels,
            key = AccountModel::id,
            contentType = { _ -> "account" }
          ) { account ->
            AccountItem(
              color = account.color.color,
              icon = account.icon.imageVector,
              name = account.name.stringValue(),
              balance = account.balance.formattedValue,
              onClick = { onAccountClick(account) },
            )
            ExpeDivider()
          }
        }
      }
    }
  }
}