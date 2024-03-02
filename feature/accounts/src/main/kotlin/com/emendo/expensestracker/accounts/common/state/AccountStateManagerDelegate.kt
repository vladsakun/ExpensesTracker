package com.emendo.expensestracker.accounts.common.state

import com.emendo.expensestracker.accounts.common.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.updateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountStateManagerDelegate<T>(defaultState: UiState<AccountScreenData<T>>? = null) : AccountStateManager<T> {

  override val _state: MutableStateFlow<UiState<AccountScreenData<T>>> =
    MutableStateFlow(defaultState ?: UiState.Loading())
  override val state: StateFlow<UiState<AccountScreenData<T>>> = _state.asStateFlow()

  override fun updateBalance(balance: Amount) {
    _state.updateData { it.copy(balance = balance) }
  }

  override fun updateCurrency(currency: CurrencyModel) {
    _state.updateData { it.copy(currency = currency) }
  }

  override fun updateIcon(icon: IconModel) {
    _state.updateData { it.copy(icon = icon) }
  }

  override fun updateColor(color: ColorModel) {
    _state.updateData { it.copy(color = color) }
  }

  override fun updateName(name: String) {
    _state.updateData { it.copy(name = name) }
  }

  override fun updateConfirmEnabled(enabled: Boolean) {
    _state.updateData { it.copy(confirmEnabled = enabled) }
  }
}