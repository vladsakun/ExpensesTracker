package com.emendo.expensestracker.accounts.common.model

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel

interface AccountScreenData {
  val name: String
  val icon: IconModel
  val color: ColorModel
  val balance: Amount
  val currency: CurrencyModel
}