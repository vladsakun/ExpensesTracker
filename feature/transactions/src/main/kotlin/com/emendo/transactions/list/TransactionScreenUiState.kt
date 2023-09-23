package com.emendo.transactions.list

import com.emendo.expensestracker.core.data.model.TransactionModel
import kotlinx.collections.immutable.ImmutableList

sealed interface TransactionScreenUiState {
  data object Loading : TransactionScreenUiState
  data class Error(val message: String) : TransactionScreenUiState
  data class DisplayTransactionsList(val transactionList: ImmutableList<TransactionModel>) : TransactionScreenUiState
}