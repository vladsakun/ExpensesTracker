package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.mapper.CurrencyMapper
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.flow.StateFlow

interface AccountStateManager {
  val state: StateFlow<AccountScreenData>

  val selectedColorId: Int
    get() = state.value.color.id

  val selectedIconId: Int
    get() = state.value.icon.id

  fun updateBalance(balance: String)
  fun updateCurrency(currency: CurrencyModel)
  fun updateName(name: String)
  fun updateConfirmEnabled(enabled: Boolean)
  fun updateIcon(icon: IconModel)
  fun updateColor(color: ColorModel)

  fun updateColorById(id: Int) {
    updateColor(ColorModel.getById(id))
  }

  fun updateCurrencyByCode(amountFormatter: AmountFormatter, currencyMapper: CurrencyMapper, code: String) {
    val currency = currencyMapper.toCurrencyModelBlocking(code)
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
    val balance = getReplacedCurrencyBalance(amountFormatter, currency)
    updateBalance(balance)
    updateCurrency(currency)
  }

  private fun getReplacedCurrencyBalance(amountFormatter: AmountFormatter, currency: CurrencyModel): String =
    amountFormatter.replaceCurrency(state.value.balance, state.value.currency, currency)
}
