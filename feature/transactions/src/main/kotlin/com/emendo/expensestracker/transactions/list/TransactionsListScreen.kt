package com.emendo.expensestracker.transactions.list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerSmallRadiusShape
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.model.ui.NetworkViewState
import com.emendo.expensestracker.model.ui.successData
import com.emendo.expensestracker.transactions.TransactionsListArgs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Destination(start = true)
@Composable
fun TransactionsListRoute(
  navigator: DestinationsNavigator,
  args: TransactionsListArgs? = null,
  viewModel: TransactionsListViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()

  TransactionsListScreenContent(
    uiStateProvider = state::value,
    onTransactionClick = { navigator.navigate(viewModel.getTransactionDetailsRoute(it)) },
    onCreateTransactionClick = { navigator.navigate(viewModel.getCreateTransactionRoute()) },
    backButton = args != null,
    onBackClick = navigator::navigateUp,
  )
}

@Composable
private fun TransactionsListScreenContent(
  uiStateProvider: () -> NetworkViewState<TransactionScreenUiState>,
  onTransactionClick: (TransactionModel) -> Unit,
  onCreateTransactionClick: () -> Unit,
  backButton: Boolean,
  onBackClick: () -> Unit,
) {
  ExpeScaffoldWithTopBar(
    titleResId = R.string.transactions,
    onNavigationClick = { onBackClick() }.takeIf { backButton },
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      when (val state = uiStateProvider()) {
        is NetworkViewState.Idle -> Unit
        is NetworkViewState.Loading -> ExpLoadingWheel()
        is NetworkViewState.Error -> Text(text = state.message.stringValue())
        is NetworkViewState.Success<*> -> TransactionsList(
          transactionsFlow = state.successData?.transactionList!!,
          onTransactionClick = onTransactionClick,
          onCreateTransactionClick = onCreateTransactionClick,
        )
      }
    }
  }
}

@Composable
private fun EmptyState(onCreateTransactionClick: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(Dimens.margin_large_x),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = stringResource(R.string.transactions_list_empty_state_title),
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(Dimens.margin_large_x),
    )
    Text(
      text = stringResource(R.string.transactions_list_empty_state_message),
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = Dimens.margin_large_x),
    )
    ExpeButton(
      text = stringResource(R.string.transactions_list_create_transaction_action),
      onClick = onCreateTransactionClick,
      modifier = Modifier
        .fillMaxWidth()
        .padding(Dimens.margin_large_x),
    )
  }
}

@Composable
private fun TransactionsList(
  transactionsFlow: Flow<PagingData<TransactionsListViewModel.UiModel>>,
  onTransactionClick: (TransactionModel) -> Unit,
  onCreateTransactionClick: () -> Unit,
) {
  val transactions: LazyPagingItems<TransactionsListViewModel.UiModel> = transactionsFlow.collectAsLazyPagingItems()

  Box(modifier = Modifier.fillMaxSize()) {
    if (transactions.itemCount == 0) {
      // Show empty state if there are no transactions
      EmptyState(onCreateTransactionClick = onCreateTransactionClick)
    } else {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        val count = transactions.itemCount
        items(count = count) { item ->
          when (val uiModel = transactions[item]) {
            is TransactionsListViewModel.UiModel.TransactionItem -> {
              TransactionItem(
                transaction = uiModel.transaction,
                onClick = { onTransactionClick(uiModel.transaction) },
              )
            }

            is TransactionsListViewModel.UiModel.SeparatorItem -> {
              SeparatorItem(separator = uiModel)
            }

            else -> Unit
          }
        }
      }
      when (transactions.loadState.refresh) {
        is LoadState.Loading -> ExpLoadingWheel(modifier = Modifier.align(Alignment.Center))
        is LoadState.Error -> Text("Error loading transaction list")
        else -> Unit
      }
    }
  }
}

@Composable
fun SeparatorItem(separator: TransactionsListViewModel.UiModel.SeparatorItem) {
  val locale = androidx.compose.ui.text.intl.Locale.current
  val text = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.FULL)
    .withLocale(Locale.forLanguageTag(locale.toLanguageTag()))
    .withZone(ZoneId.systemDefault())
    .format(separator.instant.toJavaInstant())

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = MaterialTheme.colorScheme.surface)
      .padding(vertical = Dimens.margin_small_x)
      .padding(top = Dimens.margin_large_x)
      .padding(horizontal = Dimens.margin_small_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x)
  ) {
    Text(
      text = text,
      modifier = Modifier
        .padding(vertical = Dimens.margin_small_x, horizontal = Dimens.margin_small_x)
        .weight(1f),
      style = MaterialTheme.typography.bodySmall,
    )
    separator.sum?.let { sum ->
      Text(
        text = sum,
        modifier = Modifier
          .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerSmallRadiusShape)
          .padding(vertical = Dimens.margin_small_x, horizontal = Dimens.margin_large_x),
        style = MaterialTheme.typography.bodySmall,
      )
    }
  }
  ExpeDivider()
}

@Composable
private fun TransactionItem(
  transaction: TransactionModel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isTransfer = transaction.target is AccountModel

  var expanded by remember { mutableStateOf(false) }
  val scrollState = rememberScrollState()
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentSize(Alignment.TopStart)
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .pointerInput(Unit) {
          detectTapGestures(
            onLongPress = { expanded = true },
            onTap = { onClick() }
          )
        }
        .padding(horizontal = Dimens.margin_large_x, vertical = Dimens.margin_small_x),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_xx),
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x)) {
        val target = transaction.targetSubcategory ?: transaction.target
        Icon(
          imageVector = target.icon.imageVector,
          contentDescription = "icon",
          modifier = Modifier.size(Dimens.icon_size),
          tint = target.color.color,
        )
        Text(
          text = target.name.stringValue(),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.weight(1f),
        )
        Text(
          text = transaction.amount.formattedValue,
          style = MaterialTheme.typography.bodyLarge,
          color = transaction.textColor()
        )
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isTransfer) {
          HorizontalSpacer(width = Dimens.icon_size)
          Icon(
            imageVector = ExpeIcons.SubdirectoryArrowRight,
            modifier = Modifier.size(Dimens.icon_size_small),
            contentDescription = null,
          )
        } else {
          HorizontalSpacer(width = Dimens.icon_size)
        }

        HorizontalSpacer(width = Dimens.margin_small_xx)
        Icon(
          imageVector = transaction.source.icon.imageVector,
          contentDescription = null,
          modifier = Modifier.size(Dimens.icon_size_small)
        )
        HorizontalSpacer(width = Dimens.margin_small_xx)
        Text(
          text = transaction.source.name.stringValue(),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.weight(1f),
        )
        HorizontalSpacer(width = Dimens.margin_small_xx)

        if (isTransfer) {
          transaction.transferReceivedAmount?.formattedValue?.let { amount ->
            Text(
              text = amount,
              style = MaterialTheme.typography.bodyLarge,
            )
          }
        }

        DropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false },
          properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
          ),
          scrollState = scrollState,
          offset = DpOffset(0.dp, Dimens.margin_small_x),
        ) {
          repeat(5) {
            DropdownMenuItem(
              text = { Text("Item ${it + 1}") },
              onClick = { /* TODO */ },
              leadingIcon = { Icon(imageVector = Icons.Outlined.Edit, contentDescription = null) }
            )
          }
        }
      }
      transaction.note?.let { note ->
        Text(
          text = note,
          style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
          modifier = Modifier.padding(bottom = Dimens.margin_small_x),
        )
      }
    }
  }
  ExpeDivider(
    modifier = Modifier
      .padding(start = Dimens.margin_large_x + Dimens.icon_size + Dimens.margin_small_xx)
  )
}

@Composable
@ReadOnlyComposable
private fun TransactionModel.textColor() =
  if (type == TransactionType.INCOME) MaterialTheme.customColorsPalette.successColor else LocalContentColor.current
