package com.emendo.expensestracker.transactions.list

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

sealed interface TransactionScreenUiState {
  data object Loading : TransactionScreenUiState
  data object Empty : TransactionScreenUiState
  data class Error(val message: String) : TransactionScreenUiState
  data class DisplayTransactionsList(val transactionList: Flow<PagingData<TransactionsListViewModel.UiModel>>) :
    TransactionScreenUiState
}