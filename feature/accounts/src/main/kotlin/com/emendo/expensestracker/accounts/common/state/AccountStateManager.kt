package com.emendo.expensestracker.accounts.common.state

import com.emendo.expensestracker.accounts.common.model.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import kotlinx.coroutines.flow.StateFlow

interface AccountStateManager {
  val state: StateFlow<AccountScreenData>

  val selectedColorId: Int
    get() = state.value.color.id

  val selectedIconId: Int
    get() = state.value.icon.id

  fun updateBalance(balance: Amount)
  fun updateCurrency(currency: CurrencyModel)
  fun updateName(name: String)
  fun updateConfirmEnabled(enabled: Boolean)
  fun updateIcon(icon: IconModel)
  fun updateColor(color: ColorModel)

  fun updateColorById(id: Int) {
    updateColor(ColorModel.getById(id))
  }

  fun updateCurrencyByCode(amountFormatter: AmountFormatter, code: String) {
    val currency = CurrencyModel.toCurrencyModel(code)
    setCurrency(amountFormatter, currency)
  }

  fun updateIconById(id: Int) {
    updateIcon(IconModel.getById(id))
  }

  fun setAccountName(accountName: String) {
    updateName(accountName)
    updateConfirmEnabled(state.value.name.isNotBlank())
  }

  fun setCurrency(amountFormatter: AmountFormatter, currency: CurrencyModel) {
    val balance = amountFormatter.replaceCurrency(state.value.balance, currency)
    updateBalance(balance)
    updateCurrency(currency)
  }
}
