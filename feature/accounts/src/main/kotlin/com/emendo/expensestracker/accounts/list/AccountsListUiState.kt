package com.emendo.expensestracker.accounts.list

import com.emendo.expensestracker.data.api.model.AccountModel
import kotlinx.collections.immutable.ImmutableList

sealed interface AccountsListUiState {
  data object Loading : AccountsListUiState
  data object Empty : AccountsListUiState
  data class Error(val message: String) : AccountsListUiState
  data class DisplayAccountsList(val accountModels: ImmutableList<AccountUiModel>) : AccountsListUiState
}

data class AccountUiModel(
  val accountModel: AccountModel,
  val selected: Boolean = false,
)

val AccountsListUiState.successValue: AccountsListUiState.DisplayAccountsList?
  get() = this as? AccountsListUiState.DisplayAccountsList