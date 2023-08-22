package com.emendo.accounts.create

import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel

data class CreateAccountScreenData(
  val accountName: String,
  val icon: AccountIconModel,
  val color: ColorModel,
  val initialBalance: Double,
  val currency: CurrencyModel,
) {
  companion object {
    fun getDefaultState(defaultCurrency: CurrencyModel = CurrencyModel.USD) = CreateAccountScreenData(
      accountName = "",
      icon = AccountIconModel.GROCERIES,
      color = ColorModel.GREEN,
      initialBalance = 0.0,
      currency = defaultCurrency,
    )
  }
}