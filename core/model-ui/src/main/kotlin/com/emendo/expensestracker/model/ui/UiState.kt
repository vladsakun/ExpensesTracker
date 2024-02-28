package com.emendo.expensestracker.model.ui

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Immutable
sealed class UiState<T> {
  data class Loading<T>(val text: String = "") : UiState<T>()
  data class Data<T>(val data: T) : UiState<T>()
  data class Error<T>(val message: String) : UiState<T>()
}

fun <T> UiState<T>?.dataValue(): T? {
  return (this as? UiState.Data)?.data
}

fun <T> UiState<T>?.requireDataValue(): T {
  return dataValue() ?: throw IllegalStateException("UiState.Data expected")
}

inline fun <T> MutableStateFlow<UiState<T>>.updateData(
  function: (T) -> T,
) {
  update { state ->
    if (state is UiState.Data) {
      state.copy(data = function(state.data))
    } else {
      state
    }
  }
}