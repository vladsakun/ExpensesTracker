package com.emendo.transactions.list

import android.database.Cursor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(
  transactionRepository: TransactionRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(TransactionsScreenData.getDefaultState())
  val state: StateFlow<TransactionsScreenData> = _state

  init {
    viewModelScope.launch(Dispatchers.IO) {
      transactionRepository.getAllTransactions().onEach {
        Timber.d("TransactionsScreenViewModel: $it")
      }.launchIn(viewModelScope)
    }
  }

  fun <T> Cursor.toList(block: (Cursor) -> T): List<T> {
    return mutableListOf<T>().also { list ->
      if (moveToFirst()) {
        do {
          list.add(block.invoke(this))
        } while (moveToNext())
      }
    }
  }

  fun nameChanged(name: String) {
    _state.value = _state.value.copy(accountName = name)
  }

  fun onColorChange() {
    _state.update { _state.value.copy(color = ColorModel.DEFAULT_COLOR) }
  }
}

//private fun transactionUiState(transactionRepository: TransactionRepository): Flow<TransactionScreenUiState> {
//  return transactionRepository.getAllTransactions().asResult().map {
//    when(it){
//      is Result.Loading -> TransactionScreenUiState.Loading
//      is Result.Error -> TransactionScreenUiState.Error("Error loading transactions")
//      is Result.Success -> TransactionScreenUiState.DisplayTransactionsList(it.data)
//    }
//  }
//}