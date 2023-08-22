package com.emendo.accounts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.CurrencyModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AccountsListViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
) : ViewModel() {

//  val uiState: StateFlow<AccountsListUiState> = accountsUiState(accountsRepository)
//    .stateIn(
//      scope = viewModelScope,
//      started = SharingStarted.WhileSubscribed(5_000),
//      initialValue = AccountsListUiState.Loading,
//    )

  val uiState: StateFlow<AccountsListUiState> = MutableStateFlow(getMockState())
}

// Generate the list of 10 items of Account type
fun generateAccounts(): List<Account> {
  return (1..30).map { index ->
    Account(
      id = index.toLong(),
      name = "Account $index",
      balance = 100.0,
      currencyModel = CurrencyModel.USD,
      icon = AccountIconModel.EDUCATION,
      color = ColorModel.CYAN
    )
  }
}

fun getMockState(): AccountsListUiState = AccountsListUiState.DisplayAccountsList(generateAccounts())

private fun accountsUiState(accountsRepository: AccountsRepository): Flow<AccountsListUiState> {
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