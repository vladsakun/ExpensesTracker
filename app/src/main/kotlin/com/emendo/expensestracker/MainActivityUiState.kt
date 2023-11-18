package com.emendo.expensestracker

import com.emendo.expensestracker.core.app.base.manager.AppError

sealed class MainActivityUiState {
  data object Loading : MainActivityUiState()
  data class ErrorDialog(val error: AppError) : MainActivityUiState()
}