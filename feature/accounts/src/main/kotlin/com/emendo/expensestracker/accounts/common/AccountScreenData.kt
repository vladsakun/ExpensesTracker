package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.CurrencyModel

interface AccountScreenData {
  val name: String
  val icon: IconModel
  val color: ColorModel
  val balance: String
  val currency: CurrencyModel
}