package com.emendo.expensestracker.model.ui

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Immutable
sealed class UiState<out T> {
  data class Loading<out T>(val text: String = "") : UiState<T>()
  data class Data<out T>(val data: T) : UiState<T>()
  data class Error<out T>(val message: String) : UiState<T>()
}

fun <T> UiState<T>?.dataValue(): T? {
  return (this as? UiState.Data)?.data
}

fun <T> UiState<T>?.requireDataValue(): T {
  return dataValue() ?: throw IllegalStateException("UiState.Data expected")
}

fun <D> MutableStateFlow<UiState<D>>.updateData(
  function: (D) -> D,
) {
  update { state ->
    if (state is UiState.Data) {
      state.copy(data = function(state.data))
    } else {
      state
    }
  }
}