package com.emendo.accounts.list

import com.emendo.expensestracker.core.data.model.Account

sealed interface AccountsListUiState {
  object Loading : AccountsListUiState
  object Empty : AccountsListUiState
  data class Error(val message: String) : AccountsListUiState
  data class DisplayAccountsList(val accounts: List<Account>) : AccountsListUiState
}