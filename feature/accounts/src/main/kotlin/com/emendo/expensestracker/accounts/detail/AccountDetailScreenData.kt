package com.emendo.expensestracker.accounts.detail

import com.emendo.expensestracker.accounts.common.model.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.model.ui.ColorModel

data class AccountDetailScreenData(
  override val name: String,
  override val icon: IconModel,
  override val color: ColorModel,
  override val balance: Amount,
  override val currency: CurrencyModel,
  val isConfirmAccountDetailsButtonEnabled: Boolean,
) : AccountScreenData {

  companion object {
    fun getDefaultState(accountModel: AccountModel) =
      AccountDetailScreenData(
        name = accountModel.name.value,
        icon = accountModel.icon,
        color = accountModel.color,
        balance = accountModel.balance,
        currency = accountModel.currency,
        isConfirmAccountDetailsButtonEnabled = false,
      )
  }
}