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
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.component.HorizontalSpacer
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerSmallRadiusShape
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionType
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
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
  viewModel: TransactionsListViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()

  TransactionsListScreenContent(
    uiStateProvider = state::value,
    onTransactionClick = remember { { navigator.navigate(viewModel.openTransactionDetails(it)) } }
  )
}

@Composable
private fun TransactionsListScreenContent(
  uiStateProvider: () -> TransactionScreenUiState,
  onTransactionClick: (TransactionModel) -> Unit,
) {
  ExpeScaffoldWithTopBar(titleResId = R.string.transactions) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      when (val state = uiStateProvider()) {
        is TransactionScreenUiState.Empty -> Unit
        is TransactionScreenUiState.Loading -> ExpLoadingWheel()
        is TransactionScreenUiState.Error -> Text(text = state.message)
        is TransactionScreenUiState.DisplayTransactionsList ->
          TransactionsList(
            transactionsFlow = state.transactionList,
            onTransactionClick = onTransactionClick,
          )
      }
    }
  }
}

@Composable
private fun TransactionsList(
  transactionsFlow: Flow<PagingData<TransactionsListViewModel.UiModel>>,
  onTransactionClick: (TransactionModel) -> Unit,
) {
  val transactions: LazyPagingItems<TransactionsListViewModel.UiModel> = transactionsFlow.collectAsLazyPagingItems()

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      items(count = transactions.itemCount) { movie ->
        when (val uiModel = transactions[movie]) {
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
            onLongPress = {
              expanded = !expanded
            },
            onTap = { onClick() }
          )
        }
        .padding(horizontal = Dimens.margin_large_x, vertical = Dimens.margin_small_x),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_xx),
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_x)) {
        Icon(
          imageVector = transaction.target.icon.imageVector,
          contentDescription = "icon",
          modifier = Modifier.size(Dimens.icon_size),
          tint = transaction.target.color.color,
        )
        Text(
          text = transaction.target.name.stringValue(),
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
      }
      transaction.note?.let { note ->
        Text(
          text = note,
          style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
          modifier = Modifier.padding(bottom = Dimens.margin_small_x),
        )
      }

      // Todo make dropdown menu width as wide of Row
      DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        scrollState = scrollState,
      ) {
        repeat(5) {
          DropdownMenuItem(
            text = { Text("Item ${it + 1}") },
            onClick = { /* TODO */ },
            leadingIcon = {
              Icon(
                Icons.Outlined.Edit,
                contentDescription = null
              )
            }
          )
        }
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
