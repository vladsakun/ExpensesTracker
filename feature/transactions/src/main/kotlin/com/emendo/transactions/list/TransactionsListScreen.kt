package com.emendo.transactions.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.data.model.TransactionModel
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import timber.log.Timber

@Destination(start = true)
@Composable
fun TransactionsListRoute(
  navigator: DestinationsNavigator,
  viewModel: TransactionsScreenViewModel = hiltViewModel(),
) {
  val state = viewModel.state.collectAsStateWithLifecycle()

  TransactionsListScreenContent(uiState = state)
}

@Composable
private fun TransactionsListScreenContent(
  uiState: State<TransactionScreenUiState>,
) {
  ExpeScaffoldWithTopBar(titleResId = R.string.transactions) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      when (val state = uiState.value) {
        is TransactionScreenUiState.Empty -> Unit
        is TransactionScreenUiState.Loading -> ExpLoadingWheel()
        is TransactionScreenUiState.Error -> Text(text = state.message)
        is TransactionScreenUiState.DisplayTransactionsList -> TransactionsList(
          transactions = state.transactionList
        )
      }
    }
  }
}

@Composable
private fun TransactionsList(transactions: ImmutableList<TransactionModel>) {
  Timber.d("Transactions list: $transactions")
  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(
      items = transactions,
      key = { it.id },
      contentType = { "transaction" }
    ) { transaction ->
      TransactionItem(transaction)
    }
  }
}

@Composable
private fun TransactionItem(transaction: TransactionModel) {
  val fontSize = MaterialTheme.typography.bodyLarge
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { /*TODO*/ }
      .padding(horizontal = Dimens.margin_large_x, vertical = Dimens.margin_small_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
  ) {
    Icon(
      imageVector = transaction.target.icon.imageVector,
      contentDescription = "icon",
      modifier = Modifier
        .size(Dimens.icon_size_large)
        .clip(CircleShape)
        .aspectRatio(1f)
        .clickable(onClick = {})
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
            text = transaction.target.name,
            style = fontSize,
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
              text = transaction.source.name,
              style = fontSize,
            )
          }
        }
        Spacer(modifier = Modifier.width(Dimens.margin_small_x))
        Text(
          text = transaction.formattedValue,
          style = fontSize,
          modifier = Modifier.align(Alignment.CenterVertically),
        )
      }
      Text(
        text = "Comment a bit long, but okay",
        style = fontSize.copy(fontStyle = FontStyle.Italic),
      )
    }
  }
  ExpeDivider()
}
