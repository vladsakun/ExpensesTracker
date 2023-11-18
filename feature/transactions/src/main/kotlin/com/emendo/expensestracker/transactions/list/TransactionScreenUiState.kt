package com.emendo.expensestracker.transactions.list

import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import kotlinx.collections.immutable.ImmutableList

sealed interface TransactionScreenUiState {
  data object Loading : TransactionScreenUiState
  data object Empty : TransactionScreenUiState
  data class Error(val message: String) : TransactionScreenUiState
  data class DisplayTransactionsList(val transactionList: ImmutableList<TransactionModel>) : TransactionScreenUiState
}