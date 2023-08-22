package com.emendo.accounts.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(CreateAccountScreenData.getDefaultState())
  val state = _state.asStateFlow()

  fun createNewAccount() {
//    viewModelScope.launch {
//      accountsRepository.upsertAccount(
//        Account(
//          name = accountName,
//          balance = initialBalance,
//          currencyModel = currencyModel,
//          icon = icon,
//          color = color
//        )
//      )
//    }

    // Todo
  }

  fun setAccountName(accountName: String) {
    _state.update { it.copy(accountName = accountName) }
  }

  fun setInitialBalance(initialBalance: Double) {
    _state.update { it.copy(initialBalance = initialBalance) }
  }

  fun setCurrency(currency: CurrencyModel) {
    _state.update { it.copy(currency = currency) }
  }

  fun setIcon(icon: AccountIconModel) {
    _state.update { it.copy(icon = icon) }
  }

  fun setColor(color: ColorModel) {
    _state.update { it.copy(color = color) }
  }
}