package com.emendo.expensestracker.accounts.detail

import com.emendo.expensestracker.accounts.common.model.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.model.data.CurrencyModel

data class AccountDetailScreenData(
  override val name: String,
  override val icon: IconModel,
  override val color: ColorModel,
  override val balance: String,
  override val currency: CurrencyModel,
  val isConfirmAccountDetailsButtonEnabled: Boolean,
) : AccountScreenData {

  companion object {
    fun getDefaultState(accountModel: AccountModel) =
      AccountDetailScreenData(
        name = accountModel.name.value,
        icon = accountModel.icon,
        color = accountModel.color,
        balance = accountModel.balanceFormatted,
        currency = accountModel.currency,
        isConfirmAccountDetailsButtonEnabled = false,
      )
  }
}