package com.emendo.expensestracker.accounts.common.state

import com.emendo.expensestracker.accounts.common.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.dataValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AccountStateManager<T> {
  val _state: MutableStateFlow<UiState<AccountScreenData<T>>>
  val state: StateFlow<UiState<AccountScreenData<T>>>

  val selectedColorId: Int
    get() = requireDataValue().color.id

  val selectedIconId: Int
    get() = requireDataValue().icon.id

  fun updateBalance(balance: Amount)
  fun updateCurrency(currency: CurrencyModel)
  fun updateName(name: String)
  fun updateConfirmEnabled(enabled: Boolean)
  fun updateIcon(icon: IconModel)
  fun updateColor(color: ColorModel)

  fun updateColorById(id: Int) {
    updateColor(ColorModel.getById(id))
  }

  fun updateIconById(id: Int) {
    updateIcon(IconModel.getById(id))
  }

  fun setAccountName(accountName: String) {
    updateName(accountName)
    updateConfirmEnabled(accountName.isNotBlank())
  }

  fun requireDataValue() = state.value.dataValue()!!
}
