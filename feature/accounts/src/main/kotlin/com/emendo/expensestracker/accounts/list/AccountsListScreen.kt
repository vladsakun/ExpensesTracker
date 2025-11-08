package com.emendo.expensestracker.accounts.list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.accounts.api.SelectAccountArgs
import com.emendo.expensestracker.accounts.api.SelectAccountResult
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.accounts.destinations.AccountsScreenRouteDestination
import com.emendo.expensestracker.accounts.destinations.CreateAccountRouteDestination
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.ui.AccountItem
import com.emendo.expensestracker.core.ui.AmountTextResizable
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.successData
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.scope.resultRecipient
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay

@RootNavGraph(start = true)
@Destination(
  deepLinks = [
    DeepLink(uriPattern = "https://emendo.com/accounts"),
  ],
)
@Composable
fun AccountsScreenRoute(
  navigator: DestinationsNavigator,
  resultNavigator: ResultBackNavigator<SelectAccountResult>,
  args: SelectAccountArgs? = null,
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
  val editModeState = viewModel.editMode.collectAsStateWithLifecycle()
  val totalAmountsUiState = viewModel.totalAmountsUiState.collectAsStateWithLifecycle()

  BackHandler {
    if (viewModel.editMode.value) {
      viewModel.disableEditMode()
      return@BackHandler
    }

    navigator.navigateUp()
  }

  AccountsListScreenContent(
    title = stringResource(id = viewModel.titleResId),
    uiStateProvider = uiState::value,
    totalAmountsProvider = totalAmountsUiState::value,
    onAddAccountClick = { navigator.navigate(CreateAccountRouteDestination) },
    onAccountClick = { account ->
      // Todo extract to Composition pattern
      if (viewModel.isSelectMode) {
        resultNavigator.navigateBack(SelectAccountResult(accountId = account.id, isSource = args?.isSource ?: false))
      } else {
        navigator.navigate(AccountDetailScreenDestination(account.id))
      }
    },
    onAccountLongClick = { account ->
      viewModel.enableEditMode()
      viewModel.selectAccountItem(account)
    },
    enableEditMode = viewModel::enableEditMode,
    onBackClick = onBackClick,
    editModeProvider = editModeState::value,
    onDisableEditModelClick = viewModel::disableEditMode,
    onAccountDrag = viewModel::saveAccountsOrder,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountsListScreenContent(
  title: String,
  uiStateProvider: () -> AccountsListUiState,
  totalAmountsProvider: () -> NetworkViewState<TotalAmountsUiState>,
  onAddAccountClick: () -> Unit,
  onAccountClick: (AccountModel) -> Unit,
  onAccountLongClick: (AccountModel) -> Unit,
  enableEditMode: () -> Unit,
  onBackClick: (() -> Unit)?,
  editModeProvider: () -> Boolean,
  onDisableEditModelClick: () -> Unit,
  onAccountDrag: (List<AccountUiModel>?) -> Unit,
) {
  val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  ExpeScaffold(
    topBar = {
      ExpeTopBar(
        title = title,
        scrollBehavior = topAppBarScrollBehavior,
        navigationIcon = {
          AnimatedContent(
            targetState = editModeProvider(),
            label = "navigationIcon",
          ) { editMode ->
            if (editMode) {
              IconButton(onClick = onDisableEditModelClick) {
                Icon(
                  imageVector = ExpeIcons.Close,
                  contentDescription = stringResource(id = R.string.close),
                )
              }
            } else {
              onBackClick?.let { NavigationBackIcon(onNavigationClick = onBackClick) }
            }
          }
        },
        actions = if (editModeProvider()) getEditModeActions() else getInitialModeActions(enableEditMode)
      )
    },
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
      ExtendedFloatingActionButton(
        text = { Text(text = stringResource(id = R.string.accounts_list_create_account_action)) },
        icon = {
          Icon(
            imageVector = ExpeIcons.Add,
            contentDescription = stringResource(id = R.string.accounts_list_create_account_action),
          )
        },
        onClick = onAddAccountClick,
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
          AccountListContent(
            accountModels = state.accountModels,
            totalAmountsProvider = totalAmountsProvider,
            onAccountClick = onAccountClick,
            onAccountLongClick = onAccountLongClick,
            editModeProvider = editModeProvider,
            onAccountDrag = onAccountDrag,
          )
        }
      }
    }
  }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun AccountListContent(
  accountModels: ImmutableList<AccountUiModel>,
  totalAmountsProvider: () -> NetworkViewState<TotalAmountsUiState>,
  onAccountClick: (AccountModel) -> Unit,
  onAccountLongClick: (AccountModel) -> Unit,
  editModeProvider: () -> Boolean,
  onAccountDrag: (List<AccountUiModel>?) -> Unit,
) {
  Column {
    // TODO move into a LazyColumn
    TotalAmounts(totalAmountsProvider = totalAmountsProvider)
    AccountList(
      accountModels = accountModels,
      onAccountClick = onAccountClick,
      onAccountLongClick = onAccountLongClick,
      editModeProvider = editModeProvider,
      onAccountDrag = onAccountDrag,
    )
  }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun AccountList(
  accountModels: ImmutableList<AccountUiModel>,
  onAccountClick: (AccountModel) -> Unit,
  onAccountLongClick: (AccountModel) -> Unit,
  editModeProvider: () -> Boolean,
  onAccountDrag: (List<AccountUiModel>?) -> Unit,
) {
  val list = remember(accountModels) {
    mutableStateListOf<AccountUiModel>().apply {
      addAll(accountModels)
    }
  }

  val listState = rememberLazyListState()
  val dragDropState = rememberDragDropState(
    lazyListState = listState,
    key = accountModels,
  ) { fromIndex, toIndex ->
    list.apply {
      add(toIndex, removeAt(fromIndex))
      onAccountDrag(this)
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .dragContainer(dragDropState, editModeProvider()),
    state = listState,
    horizontalAlignment = Alignment.CenterHorizontally,
    contentPadding = PaddingValues(
      bottom = Dimens.margin_large_x * 2 + Dimens.FloatingActionButtonHeight,
    ),
  ) {
    itemsIndexed(
      items = list,
      key = { _, item -> item.accountModel.id },
      contentType = { _, _ -> "account" },
    ) { index, uiModel ->
      DraggableItem(dragDropState, index) { isDragging ->
        val elevation by animateDpAsState(if (isDragging) 2.dp else 0.dp, label = "dragShadow")
        val account = uiModel.accountModel
        AccountItem(
          color = account.color.color,
          icon = account.icon.imageVector,
          name = account.name.stringValue(),
          balance = account.balance.formattedValue,
          selectedProvider = uiModel::selected,
          draggableStateProvider = editModeProvider,
          modifier = Modifier
            .shadow(elevation)
            .combinedClickable(
              onClick = if (editModeProvider()) {
                { onAccountLongClick(account) }
              } else {
                { onAccountClick(account) }
              },
              onLongClick = if (editModeProvider()) {
                null
              } else {
                { onAccountLongClick(account) }
              },
            ),
        )
        ExpeDivider()
      }
    }
  }

  val hapticFeedback = LocalHapticFeedback.current
  LaunchedEffect(editModeProvider()) {
    if (editModeProvider()) {
      hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }
}

@Composable
private fun TotalAmounts(totalAmountsProvider: () -> NetworkViewState<TotalAmountsUiState>) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(Dimens.margin_small),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small),
  ) {
    val totalAmounts = totalAmountsProvider().successData
    TotalAmount(
      amount = totalAmounts?.totalIncome,
      label = R.string.category_list_income_this_month,
      transactionType = TransactionType.INCOME,
    )
    TotalAmount(
      amount = totalAmounts?.totalExpense,
      label = R.string.category_list_expenses_this_month,
      transactionType = TransactionType.EXPENSE,
    )
  }
}

@Composable
private fun RowScope.TotalAmount(amount: Amount?, label: Int, transactionType: TransactionType) {
  val dividerColor = DividerDefaults.color
  Column(
    modifier = Modifier.Companion
      .weight(1f)
      .border(width = Dimens.border_thickness, color = dividerColor, shape = RoundedCornerNormalRadiusShape)
      .padding(Dimens.margin_small),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
  ) {
    AmountTextResizable(
      amount = amount ?: Amount.ZERO,
      textStyle = MaterialTheme.typography.titleMedium,
      transactionType = transactionType,
      modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer { this.alpha = if (amount == null) 0f else 1f },
    )
    Text(
      text = stringResource(label),
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.fillMaxWidth(),
    )
  }
}

@Composable
@ReadOnlyComposable
private fun getInitialModeActions(onClick: () -> Unit) = persistentListOf(
  MenuAction(
    icon = ExpeIcons.Edit,
    onClick = onClick,
    contentDescription = stringResource(id = R.string.edit),
  ),
)

@Composable
@ReadOnlyComposable
// TODO: Implement getEditModeActions
private fun getEditModeActions() = null
//  persistentListOf(
//  MenuAction(
//    icon = ExpeIcons.Delete,
//    onClick = {},
//    contentDescription = stringResource(id = R.string.delete),
//  ),
//  MenuAction(
//    icon = ExpeIcons.MoreVert,
//    onClick = {},
//    contentDescription = stringResource(id = R.string.more),
//  ),
//)

@Composable
fun DestinationScope<*>.selectAccountResultRecipient() =
  resultRecipient<AccountsScreenRouteDestination, SelectAccountResult>()