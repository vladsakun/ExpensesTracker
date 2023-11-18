package com.emendo.expensestracker.transactions.list

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.ui.BaseViewModel
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.repository.api.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(
  transactionsRepository: TransactionsRepository,
) : BaseViewModel() {

  val state: StateFlow<TransactionScreenUiState> = transactionUiState(transactionsRepository)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = TransactionScreenUiState.Empty,
    )
}

private fun transactionUiState(transactionsRepository: TransactionsRepository): Flow<TransactionScreenUiState> {
  return transactionsRepository.getTransactionsFull().asResult().map {
    when (it) {
      is Result.Loading -> TransactionScreenUiState.Loading
      is Result.Error -> TransactionScreenUiState.Error("Error loading transactions")
      is Result.Success -> TransactionScreenUiState.DisplayTransactionsList(it.data.toImmutableList())
      is Result.Empty -> TransactionScreenUiState.Empty
    }
  }
}