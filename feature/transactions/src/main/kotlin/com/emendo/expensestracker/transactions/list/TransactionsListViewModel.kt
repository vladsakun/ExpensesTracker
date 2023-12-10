package com.emendo.expensestracker.transactions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEvent
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import com.emendo.expensestracker.core.model.data.CreateTransactionEventPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
  transactionRepository: TransactionRepository,
  private val appNavigationEventBus: AppNavigationEventBus,
) : ViewModel() {

  val state: StateFlow<TransactionScreenUiState> = transactionUiState(transactionRepository, viewModelScope)
    .stateInWhileSubscribed(
      scope = viewModelScope,
      initialValue = TransactionScreenUiState.DisplayTransactionsList(
        transactionRepository
          .getTransactionsPager()
          .cachedIn(viewModelScope)
      ),
    )

  fun openTransactionDetails(transactionModel: TransactionModel) {
    appNavigationEventBus.navigate(
      AppNavigationEvent.CreateTransaction(
        source = transactionModel.source,
        target = transactionModel.target,
        payload = CreateTransactionEventPayload(
          transactionId = transactionModel.id,
          transactionValueFormatted = transactionModel.formattedValue,
          note = transactionModel.note,
          date = transactionModel.date,
          transactionValue = transactionModel.value,
        ),
      )
    )
  }
}

private fun transactionUiState(
  transactionRepository: TransactionRepository,
  cacheScope: CoroutineScope,
): Flow<TransactionScreenUiState> {
  return transactionRepository.getTransactionsPager().asResult().map {
    when (it) {
      is Result.Loading -> TransactionScreenUiState.Loading
      is Result.Error -> TransactionScreenUiState.Error("Error loading transactions")
      is Result.Success -> TransactionScreenUiState.DisplayTransactionsList(
        transactionRepository
          .getTransactionsPager()
          .cachedIn(cacheScope)
      )

      is Result.Empty -> TransactionScreenUiState.Empty
    }
  }
}