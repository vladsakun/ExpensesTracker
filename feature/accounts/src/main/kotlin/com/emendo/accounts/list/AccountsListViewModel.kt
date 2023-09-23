package com.emendo.accounts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
  accountsRepository: AccountsRepository,
) : ViewModel() {

  val uiState: StateFlow<AccountsListUiState> = accountsUiState(accountsRepository)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Lazily,
      initialValue = AccountsListUiState.Loading,
    )
}

private fun accountsUiState(accountsRepository: AccountsRepository): Flow<AccountsListUiState> {
  return accountsRepository.getAccounts().asResult().map { accountsResult ->
    when (accountsResult) {
      is Result.Success -> AccountsListUiState.DisplayAccountsList(accountsResult.data)
      is Result.Error -> AccountsListUiState.Error("Error loading accounts")
      is Result.Loading -> AccountsListUiState.Loading
    }
  }
}