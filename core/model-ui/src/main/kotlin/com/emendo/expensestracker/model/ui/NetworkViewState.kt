package com.emendo.expensestracker.model.ui

sealed interface NetworkViewState<out T> {
  data object Idle : NetworkViewState<Nothing>
  data object Loading : NetworkViewState<Nothing>
  data class Error(val message: TextValue) : NetworkViewState<Nothing>
  data class Success<T>(val data: T) : NetworkViewState<T>
}

val <T> NetworkViewState<T>.successData: T?
  get() = (this as? NetworkViewState.Success<T>)?.data