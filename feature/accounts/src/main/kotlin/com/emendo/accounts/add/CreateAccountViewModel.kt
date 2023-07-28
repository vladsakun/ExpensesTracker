package com.emendo.accounts.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.TopAppBarActionClickEventBus
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.model.EntityColor
import com.emendo.expensestracker.core.data.model.AccountIconResource
import com.emendo.expensestracker.core.data.model.Currency
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val topAppBarActionClickEventBus: TopAppBarActionClickEventBus,
) : ViewModel() {

  private var accountName: String? = null
  private var initialBalance: Double = 0.0
  private var currency: Currency = Currency.USD
  private var icon: AccountIconResource = AccountIconResource.EDUCATION
  private var color: EntityColor = EntityColor.BLUE

  fun createNewAccount(
    accountName: String,
    initialBalance: Double,
    currency: Currency,
    icon: AccountIconResource,
    color: EntityColor,
  ) {
    viewModelScope.launch {
      accountsRepository.upsertAccount(
        Account(
          name = accountName,
          balance = initialBalance,
          currency = currency,
          icon = icon,
          color = color
        )
      )
    }
  }

  fun registerListener() {
    topAppBarActionClickEventBus.registeredCallback = {
      accountName?.let {
        createNewAccount(it, initialBalance, currency, icon, color)
      }
    }
  }

  fun setAccountName(accountName: String) {
    this.accountName = accountName
  }
}