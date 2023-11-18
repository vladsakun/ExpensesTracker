package com.emendo.expensestracker.core.app.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.manager.AppExceptionHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.plus

abstract class BaseViewModel : ViewModel() {

  private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    AppExceptionHandler.handleException(throwable)
  }

  protected val vmScope = viewModelScope + coroutineExceptionHandler

  protected fun exceptionHandled() {
    AppExceptionHandler.exceptionHandled()
  }
}