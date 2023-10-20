package com.emendo.expensestracker.accounts.list

import com.emendo.expensestracker.core.data.model.AccountModel

sealed interface AccountsListUiState {
  data object Loading : AccountsListUiState
  data object Empty : AccountsListUiState
  data class Error(val message: String) : AccountsListUiState
  data class DisplayAccountsList(val accountModels: List<AccountModel>) : AccountsListUiState
}