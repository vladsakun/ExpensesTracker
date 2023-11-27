package com.emendo.expensestracker.core.app.base.manager

//object AppExceptionHandler {
//  // add queue for errors
//
//  private val _errorState = MutableStateFlow<_root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.AppError?>(null)
//
//  fun subscribeErrors(): Flow<_root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.AppError?> = _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.AppExceptionHandler._errorState
//
//  fun handleException(throwable: Throwable) {
//    val appError = when (throwable) {
//      is CurrencyRateNotFoundException -> {
//        _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.AppError(
//          title = "Currency rates are absent",
//          message = "We need to fetch currency rates. Without currency rates we can't convert currencies 🔄",
//          positiveAction = _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.Action(
//            text = "Retry",
//            action = _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.ActionOperation.Execute(throwable.actionable.action())
//          ),
//          negativeAction = _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.Action(
//            text = "Cancel",
//            action = _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.ActionOperation.Dismiss
//          )
//        )
//      }
//
//      else -> null
//    }
//
//    _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.AppExceptionHandler._errorState.update { appError }
//  }
//
//  fun exceptionHandled() {
//    _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.AppExceptionHandler._errorState.update { null }
//  }
//}
//
//data class AppError(
//  val title: String,
//  val message: String,
//  val positiveAction: _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.Action,
//  val negativeAction: _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.Action,
//)
//
//data class Action(
//  val text: String,
//  val action: _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.ActionOperation,
//)
//
//sealed interface ActionOperation {
//  data object Dismiss : _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.ActionOperation
//  data class Execute(val action: suspend () -> Unit) : _root_ide_package_.com.emendo.expensestracker.core.data.app.base.manager.ActionOperation
//}