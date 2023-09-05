package com.emendo.accounts.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.accounts.destinations.CreateAccountRouteDestination
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.divider_color
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@Destination(start = true)
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

  val accountsListUiState: AccountsListUiState by viewModel.uiState.collectAsStateWithLifecycle()
  AccountsListScreenContent(accountsListUiState) { navigator.navigate(CreateAccountRouteDestination) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountsListScreenContent(
  uiState: AccountsListUiState,
  onAddAccountClick: () -> Unit,
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  ExpeScaffold(
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    topBar = {
      ExpeTopBar(
        titleRes = R.string.accounts,
        scrollBehavior = topAppBarScrollBehavior,
      )
    },
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
      FloatingActionButton(
        onClick = onAddAccountClick,
        content = {
          Icon(
            imageVector = ExpIcons.Add,
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
      when (uiState) {
        is AccountsListUiState.Loading -> item {
          ExpLoadingWheel(contentDesc = stringResource(id = R.string.accounts_loading))
        }

        is AccountsListUiState.Empty -> item {
          Text(text = "Empty")
        }

        is AccountsListUiState.Error -> item {
          Text(text = uiState.message)
        }

        is AccountsListUiState.DisplayAccountsList -> {
          items(
            items = uiState.accounts,
            key = { it.id },
            contentType = { _ -> "accounts" }
          ) { account ->
            AccountItem(account)
          }
        }
      }
    }
  }
}

@Composable
private fun AccountItem(account: Account) {
  Column {
    Row(
      modifier = Modifier.padding(Dimens.margin_small_x),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x)
    ) {
      Icon(
        modifier = Modifier
          .clip(RoundedCornerShape(Dimens.corner_radius_small))
          .background(color = account.color.color.copy(alpha = 0.2f))
          .border(
            width = Dimens.border_thickness,
            color = account.color.color,
            shape = RoundedCornerShape(Dimens.corner_radius_small)
          )
          .padding(Dimens.margin_small_x),
        imageVector = account.icon.imageVector,
        contentDescription = "",
      )
      Text(
        text = account.name,
        modifier = Modifier
          .fillMaxHeight()
          .weight(1f),
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        modifier = Modifier
          .fillMaxHeight()
          .weight(2f),
        text = account.formattedBalance,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.End,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
      )
    }
    Divider(
      modifier = Modifier.padding(start = Dimens.icon_size + Dimens.margin_small_x * 4),
      color = divider_color,
      thickness = Dimens.divider_thickness
    )
  }
}