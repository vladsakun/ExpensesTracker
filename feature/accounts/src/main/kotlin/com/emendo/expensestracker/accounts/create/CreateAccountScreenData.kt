package com.emendo.expensestracker.accounts.create

import com.emendo.expensestracker.accounts.common.model.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.model.ui.ColorModel

data class CreateAccountScreenData(
  override val name: String,
  override val icon: IconModel,
  override val color: ColorModel,
  override val balance: Amount,
  override val currency: CurrencyModel,
  val isCreateAccountButtonEnabled: Boolean,
) : AccountScreenData {

  companion object {
    fun getDefaultState(currency: CurrencyModel, balance: Amount) =
      CreateAccountScreenData(
        name = "",
        icon = IconModel.random,
        color = ColorModel.random,
        balance = balance,
        currency = currency,
        isCreateAccountButtonEnabled = false,
      )
  }
}