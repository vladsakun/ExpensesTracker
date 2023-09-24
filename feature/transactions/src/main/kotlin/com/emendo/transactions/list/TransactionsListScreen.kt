package com.emendo.transactions.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.data.model.TransactionModel
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
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
      Text(text = "Source name: ${transaction.source.name}")
      Text(text = "Target name: ${transaction.target.name}")
      Text(text = "Value: ${transaction.value}")
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
