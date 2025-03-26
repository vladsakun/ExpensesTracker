package com.emendo.expensestracker.core.app.common

sealed interface NetworkViewState<out T> {
  data object Loading : NetworkViewState<Nothing>
  data class Error(val message: String) : NetworkViewState<Nothing>
  data class Success<T>(val data: T) : NetworkViewState<T>
}

val <T> NetworkViewState<T>.successData: T?
  get() = (this as? NetworkViewState.Success<T>)?.data