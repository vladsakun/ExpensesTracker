//package com.emendo.expensestracker
//
//import com.emendo.expensestracker.core.data.app.base.manager.AppError
//
//sealed class MainActivityUiState {
//  data object Loading : MainActivityUiState()
//  data class ErrorDialog(val error: AppError) : MainActivityUiState()
//}