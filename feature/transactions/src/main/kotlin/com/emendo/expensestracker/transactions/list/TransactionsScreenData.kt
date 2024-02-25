package com.emendo.expensestracker.transactions.list

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel

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
        color = ColorModel.DEFAULT_COLOR,
      )
  }
}