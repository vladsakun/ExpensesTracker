package com.emendo.accounts.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.accounts.destinations.AddAccountScreenDestination
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.emendo.expensestracker.feature.accounts.R
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect

@RootNavGraph(start = true)
@Destination(start = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
  navigator: DestinationsNavigator,
  viewModel: AccountsListViewModel = hiltViewModel(),
) {
  val accountsListUiState: AccountsListUiState by viewModel.uiState.collectAsStateWithLifecycle()
  viewModel.registerListener()

  LaunchedEffect(true) {
    viewModel.navigationEvent.collect {
      if (it != null) {
        navigator.navigate(AddAccountScreenDestination)
      }
    }
  }

  AccountsListScreenContent(accountsListUiState)
}

@Composable
private fun AccountsListScreenContent(accountsListUiState: AccountsListUiState) {
  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
  ) {
    item {
      Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
    }

    when (accountsListUiState) {
      is AccountsListUiState.Loading -> item {
        ExpLoadingWheel(
          contentDesc = stringResource(id = R.string.accounts_loading)
        )
      }

      is AccountsListUiState.Empty -> TODO()
      is AccountsListUiState.Error -> TODO()
      is AccountsListUiState.DisplayAccountsList -> {
        items(
          items = accountsListUiState.accounts,
          key = { it.id }
        ) { account ->
          AccountItem(account)
        }
      }
    }

    item {
      Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
    }
  }
}

@Composable
private fun AccountItem(account: Account) {
  Row(
    modifier = Modifier.padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(
      imageVector = account.icon.imageVector,
      contentDescription = "",
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = account.name)
  }
}