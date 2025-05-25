package com.emendo.expensestracker.transactions.list

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class TransactionScreenUiState(val transactionList: Flow<PagingData<TransactionsListViewModel.UiModel>>)