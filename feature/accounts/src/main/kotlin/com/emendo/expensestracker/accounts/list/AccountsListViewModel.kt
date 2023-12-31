package com.emendo.expensestracker.accounts.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.common.ext.stateInLazily
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
  accountRepository: AccountRepository,
  private val createTransactionRepository: CreateTransactionRepository,
) : ViewModel() {

  val uiState: StateFlow<AccountsListUiState> = accountsUiState(accountRepository)
    .stateInLazily(
      scope = viewModelScope,
      initialValue = AccountsListUiState.DisplayAccountsList(accountRepository.getAccountsSnapshot().toImmutableList()),
    )

  val isSelectMode: Boolean
    get() = createTransactionRepository.isSelectMode()
  val titleResId: Int
    @StringRes get() = if (isSelectMode) R.string.select_account else R.string.accounts

  fun selectAccountItem(account: AccountModel) {
    createTransactionRepository.selectAccount(account)
  }

  override fun onCleared() {
    createTransactionRepository.finishSelectMode()
  }
}

private fun accountsUiState(accountRepository: AccountRepository): Flow<AccountsListUiState> {
  return accountRepository.getAccounts().asResult().map { accountsResult ->
    when (accountsResult) {
      is Result.Success -> AccountsListUiState.DisplayAccountsList(accountsResult.data.toImmutableList())
      is Result.Error -> AccountsListUiState.Error("Error loading accounts")
      is Result.Loading -> AccountsListUiState.Loading
      is Result.Empty -> AccountsListUiState.Empty
    }
  }
}