package com.emendo.expensestracker.createtransaction.selectaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.base.manager.CreateTransactionRepository
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.result.Result
import com.emendo.expensestracker.core.app.common.result.asResult
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

val mockAccounts = persistentListOf(
  AccountModel(
    id = 1,
    currency = CurrencyModel(
      currencyCode = "USD",
      currencyName = "Dollar",
      currencySymbol = null
    ),
    name = TextValue.Value("Norma Reid"),
    icon = IconModel.CREDITCARD,
    color = ColorModel.Purple,
    balance = BigDecimal.TEN,
    balanceFormatted = "$ 10",
  ),
  AccountModel(
    id = 2,
    currency = CurrencyModel(
      currencyCode = "USD",
      currencyName = "Dollar",
      currencySymbol = null
    ),
    name = TextValue.Value("Norma Reid"),
    icon = IconModel.CREDITCARD,
    color = ColorModel.Purple,
    balance = BigDecimal.TEN,
    balanceFormatted = "$ 20",
  ),
  AccountModel(
    id = 3,
    currency = CurrencyModel(
      currencyCode = "USD",
      currencyName = "Dollar",
      currencySymbol = null
    ),
    name = TextValue.Value("Norma Reid"),
    icon = IconModel.CREDITCARD,
    color = ColorModel.Purple,
    balance = BigDecimal.TEN,
    balanceFormatted = "$ 30",
  ),
)

@HiltViewModel
class SelectAccountViewModel @Inject constructor(
  accountRepository: AccountRepository,
  private val createTransactionRepository: CreateTransactionRepository,
) : ViewModel() {

  val uiState = accountsUiState(accountRepository)
    .stateInWhileSubscribed(
      scope = viewModelScope,
      initialValue = getSuccessState(accountRepository.accountsSnapshot),
    )

  fun selectAccount(account: AccountModel) {
    createTransactionRepository.setSource(account)
  }
}

private fun getSuccessState(accountModels: List<AccountModel>): SelectAccountUiState {
  if (accountModels.isEmpty()) {
    return SelectAccountUiState.Empty
  }

  return SelectAccountUiState.DisplayAccountsList(accountModels.toImmutableList())
}

private fun accountsUiState(accountRepository: AccountRepository): Flow<SelectAccountUiState> {
  return accountRepository.accounts.asResult().map { accountsResult ->
    when (accountsResult) {
      is Result.Success -> getSuccessState(accountsResult.data.toImmutableList())
      is Result.Error -> SelectAccountUiState.Error("Error loading accounts")
      is Result.Loading -> SelectAccountUiState.Loading
      is Result.Empty -> SelectAccountUiState.Default
    }
  }
}