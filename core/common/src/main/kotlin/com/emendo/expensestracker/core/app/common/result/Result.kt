package com.emendo.expensestracker.core.app.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

sealed interface Result<out T> {
  data class Success<T>(val data: T) : Result<T>
  data class Error(val exception: Throwable? = null) : Result<Nothing>
  data object Loading : Result<Nothing>
  data object Idle : Result<Nothing>
}

fun <T> Flow<T>.asResult(isLocalOnly: Boolean = true): Flow<Result<T>> {
  return this
    .map<T, Result<T>> {
      Result.Success(it)
    }
    // Todo pass Loading if needed from server some time.
    // Now it emits Empty, so the screen will be blank without loader flickering
    .onStart { if (!isLocalOnly) emit(Result.Loading) }
    .catch { throwable ->
      Timber.e(throwable)
      emit(Result.Error(throwable))
    }
}