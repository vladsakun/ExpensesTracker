package com.emendo.expensestracker.core.app.base.manager

import com.emendo.expensestracker.core.app.base.exception.CurrencyRateNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object AppExceptionHandler {
  // add queue for errors

  private val _errorState = MutableStateFlow<AppError?>(null)

  fun subscribeErrors(): Flow<AppError?> = _errorState

  fun handleException(throwable: Throwable) {
    val appError = when (throwable) {
      is CurrencyRateNotFoundException -> {
        AppError(
          title = "Currency rates are absent",
          message = "We need to fetch currency rates. Without currency rates we can't convert currencies 🔄",
          positiveAction = Action(
            text = "Retry",
            action = ActionOperation.Execute(throwable.actionable.action())
          ),
          negativeAction = Action(
            text = "Cancel",
            action = ActionOperation.Dismiss
          )
        )
      }

      else -> null
    }

    _errorState.update { appError }
  }

  fun exceptionHandled() {
    _errorState.update { null }
  }
}

data class AppError(
  val title: String,
  val message: String,
  val positiveAction: Action,
  val negativeAction: Action,
)

data class Action(
  val text: String,
  val action: ActionOperation,
)

sealed interface ActionOperation {
  data object Dismiss : ActionOperation
  data class Execute(val action: suspend () -> Unit) : ActionOperation
}