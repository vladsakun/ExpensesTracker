package com.emendo.accounts.list

import com.emendo.expensestracker.core.data.model.AccountModel

sealed interface AccountsListUiState {
  data object Loading : AccountsListUiState
  data class Error(val message: String) : AccountsListUiState
  data class DisplayAccountsList(val accountModels: List<AccountModel>) : AccountsListUiState
}