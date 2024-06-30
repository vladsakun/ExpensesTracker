package com.emendo.expensestracker.accounts.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.common.ext.stateInLazily
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import com.emendo.expensestracker.core.model.data.AccountWithOrdinalIndex
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
  private val accountRepository: AccountRepository,
  private val createTransactionController: CreateTransactionController,
  @Dispatcher(ExpeDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

  private val selectedAccounts: MutableStateFlow<Set<AccountModel>> = MutableStateFlow(emptySet())

  val uiState: StateFlow<AccountsListUiState> =
    combine(accountsUiState(accountRepository), selectedAccounts) { uiState, selectedAccounts ->
      if (uiState is AccountsListUiState.DisplayAccountsList) {
        val accountModels = (orderedAccounts ?: uiState.accountModels).map { accountUiModel ->
          accountUiModel.copy(selected = selectedAccounts.contains(accountUiModel.accountModel))
        }
        AccountsListUiState.DisplayAccountsList(accountModels.toImmutableList())
      } else {
        uiState
      }
    }.stateInLazily(
      scope = viewModelScope,
      initialValue = AccountsListUiState.DisplayAccountsList(
        accountRepository
          .getAccountsSnapshot()
          .map(::AccountUiModel)
          .toImmutableList()
      ),
    )

  private val _editMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
  internal val editMode: StateFlow<Boolean> = _editMode

  val isSelectMode: Boolean
    get() = createTransactionController.isSelectMode()
  val titleResId: Int
    @StringRes get() = if (isSelectMode) R.string.select_account else R.string.accounts

  private var orderedAccounts: List<AccountUiModel>? = null

  fun pickAccountItem(account: AccountModel) {
    createTransactionController.selectAccount(account)
  }

  internal fun enableEditMode() {
    _editMode.update { true }
  }

  fun selectAccountItem(account: AccountModel) {
    selectedAccounts.update { accounts ->
      accounts.toMutableSet().apply {
        if (contains(account)) {
          remove(account)
        } else {
          add(account)
        }
      }
    }
  }

  private fun updateAccountsIndexes(eventsToHandle: List<AccountUiModel>?) =
    viewModelScope.launch(defaultDispatcher) {
      if (eventsToHandle == null) {
        return@launch
      }

      val accounts: List<AccountUiModel> = uiState.value.successValue?.accountModels ?: return@launch
      val diff: MutableSet<AccountWithOrdinalIndex> = mutableSetOf()

      accounts.forEachIndexed { index, uiModel ->
        val account = uiModel.accountModel
        val newOrderedAccountByIndex = eventsToHandle[index].accountModel
        if (account.id != newOrderedAccountByIndex.id) {
          diff.add(
            AccountWithOrdinalIndex(
              id = newOrderedAccountByIndex.id,
              ordinalIndex = account.ordinalIndex,
            )
          )
        }
      }

      if (diff.isEmpty()) {
        return@launch
      }

      accountRepository.updateOrdinalIndex(diff)
    }

  fun disableEditMode() {
    _editMode.update { false }
    selectedAccounts.update { mutableSetOf() }
    updateAccountsIndexes(orderedAccounts)
  }

  fun saveAccountsOrder(accountUiModels: List<AccountUiModel>?) {
    orderedAccounts = accountUiModels
  }

  override fun onCleared() {
    createTransactionController.finishSelectMode()
  }
}

private fun accountsUiState(accountRepository: AccountRepository): Flow<AccountsListUiState> {
  return accountRepository.getAccounts().asResult().map { accountsResult ->
    when (accountsResult) {
      is Result.Success -> AccountsListUiState.DisplayAccountsList(
        accountsResult.data
          .map(::AccountUiModel)
          .sortedBy { it.accountModel.ordinalIndex }
          .toImmutableList()
      )

      is Result.Error -> AccountsListUiState.Error("Error loading accounts")
      is Result.Loading -> AccountsListUiState.Loading
      is Result.Empty -> AccountsListUiState.Empty
    }
  }
}