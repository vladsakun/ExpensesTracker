package com.emendo.expensestracker.core.app.common.result

import com.emendo.expensestracker.core.model.data.exception.ActionableException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Result<out T> {
  data class Success<T>(val data: T) : Result<T>
  data class Error(val exception: Throwable? = null) : Result<Nothing>
  data object Loading : Result<Nothing>
  data object Empty : Result<Nothing>
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
      if (throwable is ActionableException) {
        //        throw throwable
      }

      emit(Result.Error(throwable))
    }
}

// expeRunCatching {
//  fetchCurrencies()
// }

// ExpeRunCatching {
//   getCurrenciesList()
// }

// if no internet -> Show NO INTERNET dialog with retry action (lambda)
// if io exception -> Show ERROR dialog with retry action (lambda)
// else -> Show GENERAL ERROR