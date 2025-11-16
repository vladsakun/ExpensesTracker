package com.emendo.expensestracker.model.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface NetworkViewState<out T> {
  data object Idle : NetworkViewState<Nothing>
  data object Loading : NetworkViewState<Nothing>
  data class Error(val message: TextValue) : NetworkViewState<Nothing>
  data class Success<T>(val data: T) : NetworkViewState<T>
}

val <T> NetworkViewState<T>.successData: T?
  get() = (this as? NetworkViewState.Success<T>)?.data

fun <D> MutableStateFlow<NetworkViewState<D>>.updateData(
  function: (D) -> D,
) {
  update { state ->
    if (state is NetworkViewState.Success) {
      state.copy(data = function(state.data))
    } else {
      state
    }
  }
}