package com.emendo.expensestracker.accounts.common.state

import com.emendo.expensestracker.accounts.common.model.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AccountStateManagerDelegate<T>(defaultScreenData: AccountScreenData<T>) : AccountStateManager<T> {

  private val _state: MutableStateFlow<AccountScreenData<T>> = MutableStateFlow(defaultScreenData)
  override val state: StateFlow<AccountScreenData<T>> = _state.asStateFlow()

  override fun updateBalance(balance: Amount) {
    _state.update { it.copy(balance = balance) }
  }

  override fun updateCurrency(currency: CurrencyModel) {
    _state.update { it.copy(currency = currency) }
  }

  override fun updateIcon(icon: IconModel) {
    _state.update { it.copy(icon = icon) }
  }

  override fun updateColor(color: ColorModel) {
    _state.update { it.copy(color = color) }
  }

  override fun updateName(name: String) {
    _state.update { it.copy(name = name) }
  }

  override fun updateConfirmEnabled(enabled: Boolean) {
    _state.update { it.copy(confirmEnabled = enabled) }
  }
}