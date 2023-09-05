package com.emendo.transactions

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel

data class TransactionsScreenData(
  val accountName: String,
  val icon: IconModel,
  val color: ColorModel,
) {
  companion object {
    fun getDefaultState() =
      TransactionsScreenData(
        accountName = "",
        icon = IconModel.GROCERIES,
        color = ColorModel.GREEN,
      )
  }
}