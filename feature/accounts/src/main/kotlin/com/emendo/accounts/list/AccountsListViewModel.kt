package com.emendo.accounts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.TopAppBarActionClickEventBus
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.model.AccountColor
import com.emendo.expensestracker.core.data.model.AccountIconResource
import com.emendo.expensestracker.core.data.model.Currency
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val topAppBarActionClickEventBus: TopAppBarActionClickEventBus,
) : ViewModel() {

  private val _navigationChannel = Channel<Unit?>(Channel.CONFLATED)
  val navigationEvent: Flow<Unit?> = _navigationChannel.receiveAsFlow()

  val uiState: StateFlow<AccountsListUiState> = accountsUiState(accountsRepository)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = AccountsListUiState.Loading,
    )

  fun registerListener() {
    topAppBarActionClickEventBus.registeredCallback = {
      viewModelScope.launch(Dispatchers.IO) {
        _navigationChannel.send(Unit)
      }
    }
  }
}

// Generate the list of 10 items of Account type
fun generateAccounts(): List<Account> {
  return (1..30).map { index ->
    Account(
      id = index.toLong(),
      name = "Account $index",
      balance = 100.0,
      currency = Currency.USD,
      icon = AccountIconResource.EDUCATION,
      color = AccountColor.CYAN
    )
  }
}

fun getMockState(): AccountsListUiState = AccountsListUiState.DisplayAccountsList(generateAccounts())

private fun accountsUiState(accountsRepository: AccountsRepository): Flow<AccountsListUiState> {
  //  return flowOf(
  //    AccountsListUiState.DisplayAccountsList(
  //      accounts = generateAccounts()
  //    )
  //  )

  return accountsRepository.getAccounts().asResult().map { accountsResult ->
    when (accountsResult) {
      is Result.Success -> {
        AccountsListUiState.DisplayAccountsList(accountsResult.data)
      }

      is Result.Error -> {
        AccountsListUiState.Error("Error loading accounts")
      }

      is Result.Loading -> {
        AccountsListUiState.Loading
      }
    }
  }
}