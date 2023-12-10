package com.emendo.expensestracker.transactions.list

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionTargetUiModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionType
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.customColorsPalette
import com.emendo.expensestracker.core.ui.stringValue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow

@Destination(start = true)
@Composable
fun TransactionsListRoute(
  navigator: DestinationsNavigator,
  viewModel: TransactionsListViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()

  TransactionsListScreenContent(
    uiStateProvider = state::value,
    onTransactionClick = remember { viewModel::openTransactionDetails }
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
  transactionsFlow: Flow<PagingData<TransactionModel>>,
  onTransactionClick: (TransactionModel) -> Unit,
) {
  val transactions: LazyPagingItems<TransactionModel> = transactionsFlow.collectAsLazyPagingItems()
  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(tween(400))
    ) {
      items(
        count = transactions.itemCount,
        key = { index -> transactions[index]?.id ?: 0 },
        contentType = { "transaction" }
      ) { index ->
        val transaction = transactions[index] ?: return@items
        TransactionItem(
          transaction = transaction,
          onClick = { onTransactionClick(transaction) },
        )
      }
    }
    when (transactions.loadState.refresh) {
      is LoadState.Loading -> ExpLoadingWheel(modifier = Modifier.align(Alignment.Center))
      else -> Unit
    }
  }
}

@Composable
private fun TransactionItem(
  transaction: TransactionModel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = Dimens.margin_large_x)
      .padding(top = Dimens.margin_small_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
  ) {
    Icon(
      imageVector = transaction.target.icon.imageVector,
      contentDescription = "icon",
      modifier = Modifier
        .size(Dimens.icon_size_large)
        .clip(CircleShape)
        .aspectRatio(1f)
        .background(color = transaction.target.color.color)
        .padding(Dimens.margin_small_x),
    )
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_xx),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_xx),
        ) {
          Text(
            text = transaction.target.name.stringValue(),
            style = MaterialTheme.typography.bodyLarge,
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.margin_small_xx),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = transaction.source.icon.imageVector,
              contentDescription = "source_icon",
              modifier = Modifier.size(Dimens.icon_size_small)
            )
            Text(
              text = transaction.source.name.stringValue(),
              style = MaterialTheme.typography.bodyLarge,
            )
          }
        }
        Spacer(modifier = Modifier.width(Dimens.margin_small_x))
        Text(
          text = transaction.formattedValue,
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.align(Alignment.CenterVertically),
          color = transaction.textColor()
        )
        if (transaction.target is TransactionTargetUiModel.Account) {
          Text(
            text = transaction.transferReceivedValue.toString(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterVertically),
          )
        }
      }
      transaction.note?.let { note ->
        Text(
          text = note,
          style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
          modifier = Modifier.padding(bottom = Dimens.margin_small_x)
        )
      }
      ExpeDivider()
    }
  }
}

@Composable
@ReadOnlyComposable
private fun TransactionModel.textColor() =
  if (type == TransactionType.INCOME) MaterialTheme.customColorsPalette.successColor else LocalContentColor.current
