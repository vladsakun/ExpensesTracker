package com.emendo.transactions

import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel

data class TransactionsScreenData(
  val accountName: String,
  val icon: AccountIconModel,
  val color: ColorModel,
) {
  companion object {
    fun getDefaultState() =
      TransactionsScreenData(
        accountName = "",
        icon = AccountIconModel.GROCERIES,
        color = ColorModel.GREEN,
      )
  }
}