package com.emendo.expensestracker

import com.emendo.expensestracker.core.app.base.manager.ActionOperation
import com.emendo.expensestracker.core.app.base.manager.AppError
import com.emendo.expensestracker.core.app.base.manager.AppExceptionHandler
import com.emendo.expensestracker.core.app.base.ui.BaseViewModel
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel(), AppStateCommander {

  val errorState = AppExceptionHandler
    .subscribeErrors()
    .onEach(::handleAppError)
    .stateIn(vmScope, SharingStarted.WhileSubscribed(5_000L), null)

  private val _state = MutableStateFlow<MainActivityUiState?>(null)
  val uiSState: StateFlow<MainActivityUiState?> = _state

  override fun onPositiveActionClick() {
    val appError: AppError = (_state.value as? MainActivityUiState.ErrorDialog)?.error ?: return

    when (val error = appError.positiveAction.action) {
      is ActionOperation.Dismiss -> onNegativeActionClick()
      is ActionOperation.Execute -> {
        _state.update { MainActivityUiState.Loading }
        vmScope.launch(ioDispatcher) {
          exceptionHandled()
          error.action.invoke()
          _state.update { null }
        }
      }
    }
  }

  override fun onNegativeActionClick() {
    exceptionHandled()
    _state.update { null }
  }

  override fun onAlertDialogDismissRequest() {
    exceptionHandled()
    _state.update { null }
  }

  private fun handleAppError(appError: AppError?) {
    val error = appError ?: return
    _state.update { MainActivityUiState.ErrorDialog(error) }
  }
}

