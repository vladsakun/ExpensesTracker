package com.emendo.accounts.list

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.accounts.destinations.CreateAccountRouteDestination
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffold
import com.emendo.expensestracker.core.designsystem.component.ExpeTopAppBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.divider_color
import com.emendo.expensestracker.feature.accounts.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val TAG = "AccountsListScreen"

@Destination(start = true)
@Composable
fun AccountsScreen(
  navigator: DestinationsNavigator,
  viewModel: AccountsListViewModel = hiltViewModel(),
) {
  val accountsListUiState: AccountsListUiState by viewModel.uiState.collectAsStateWithLifecycle()
  AccountsListScreenContent(accountsListUiState) { navigator.navigate(CreateAccountRouteDestination) }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
      ExpeTopAppBar(
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
        .padding(padding)
        .consumeWindowInsets(padding),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      when (uiState) {
        is AccountsListUiState.Loading -> item {
          ExpLoadingWheel(
            contentDesc = stringResource(id = R.string.accounts_loading)
          )
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
      modifier = Modifier
        .padding(Dimens.margin_small_x),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Image(
        modifier = Modifier
          .clip(RoundedCornerShape(Dimens.corner_radius_small))
          .background(color = account.color.color.copy(alpha = 0.65f))
          .border(
            width = Dimens.border_thickness,
            color = account.color.color,
            shape = RoundedCornerShape(Dimens.corner_radius_small)
          )
          .padding(Dimens.margin_small_x),
        imageVector = account.icon.imageVector,
        contentDescription = "",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inverseOnSurface),
      )
      Spacer(modifier = Modifier.width(Dimens.margin_small_x))
      Text(
        modifier = Modifier.fillMaxHeight(),
        text = account.name,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium
      )
    }
    Divider(
      modifier = Modifier.padding(
        start = Dimens.icon_size + Dimens.margin_small_x * 4
      ),
      color = divider_color,
      thickness = Dimens.divider_thickness
    )
  }
}