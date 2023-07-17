package com.emendo.accounts.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.result.TopAppBarActionClickEventBus
import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.model.AccountColor
import com.emendo.expensestracker.core.data.model.AccountIconResource
import com.emendo.expensestracker.core.data.model.Currency
import com.emendo.expensestracker.core.data.repository.AccountsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val topAppBarActionClickEventBus: TopAppBarActionClickEventBus,
) : ViewModel() {

  var accountName: String? = null

  fun addAccount(accountName: String) {
    viewModelScope.launch {
      accountsRepository.upsertAccount(
        Account(
          name = accountName,
          balance = 0.0,
          currency = Currency.USD,
          icon = AccountIconResource.getById(1),
          color = AccountColor.getById(1)
        )
      )
    }
  }

  fun registerListener() {
    topAppBarActionClickEventBus.registeredCallback = {
      accountName?.let {
        addAccount(it)
      }
    }
  }
}