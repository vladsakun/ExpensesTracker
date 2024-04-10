package com.emendo.expensestracker.accounts.list

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.accounts.destinations.CreateAccountRouteDestination
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.ui.AccountItem
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

@RootNavGraph(start = true)
@Destination(
  deepLinks = [
    DeepLink(
      uriPattern = "https://emendo.com/accounts"
    ),
  ],
)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    modifier = Modifier.fillMaxSize(),
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
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
      contentAlignment = Alignment.Center,
    ) {
      when (val state = uiStateProvider()) {
        is AccountsListUiState.Empty -> Unit
        is AccountsListUiState.Loading -> ExpLoadingWheel()
        is AccountsListUiState.Error -> Text(text = state.message)
        is AccountsListUiState.DisplayAccountsList -> {
          AccountList(onAccountClick, state.accountModels, {})
        }
      }
    }
  }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun AccountList(
  onAccountClick: (AccountModel) -> Unit,
  accountModels: ImmutableList<AccountModel>,
  onMove: (List<AccountModel>) -> Unit,
) {
  val mutableList = remember(accountModels) {
    mutableStateListOf<AccountModel>().apply {
      addAll(accountModels.toList())
    }
  }

  val listState = rememberLazyListState()
  val dragDropState = rememberDragDropState(
    lazyListState = listState,
    key = accountModels,
  ) { fromIndex, toIndex ->
    with(mutableList) {
      add(toIndex, removeAt(fromIndex))
      onMove(mutableList)
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .dragContainer(dragDropState),
    state = listState,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    itemsIndexed(
      items = accountModels,
      key = { _, item -> item.id },
      contentType = { _, _ -> "account" },
    ) { index, account ->
      DraggableItem(dragDropState, index) { isDragging ->
        val elevation by animateDpAsState(if (isDragging) 4.dp else 1.dp, label = "dragShadow")
        Card(elevation = CardDefaults.cardElevation(elevation)) {
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