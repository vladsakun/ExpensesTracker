package com.emendo.transactions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(
  transactionRepository: TransactionRepository,
) : ViewModel() {

  val state: StateFlow<TransactionScreenUiState> = transactionUiState(transactionRepository)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = TransactionScreenUiState.Empty,
    )
}

private fun transactionUiState(transactionRepository: TransactionRepository): Flow<TransactionScreenUiState> {
  return transactionRepository.getTransactionsFull().asResult().map {
    when (it) {
      is Result.Loading -> TransactionScreenUiState.Loading
      is Result.Error -> TransactionScreenUiState.Error("Error loading transactions")
      is Result.Success -> TransactionScreenUiState.DisplayTransactionsList(it.data.toImmutableList())
      is Result.Empty -> TransactionScreenUiState.Empty
    }
  }
}