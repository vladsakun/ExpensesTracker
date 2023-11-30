package com.emendo.expensestracker.accounts.create

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.keyboard.CalculatorConstants.INITIAL_CALCULATOR_TEXT

data class CreateAccountScreenData(
  val accountName: String,
  val icon: IconModel,
  val color: ColorModel,
  val initialBalance: String,
  val currency: CurrencyModel,
  val isCreateAccountButtonEnabled: Boolean,
) {

  companion object {
    fun getDefaultState(currency: CurrencyModel) =
      CreateAccountScreenData(
        accountName = "",
        icon = IconModel.random,
        color = ColorModel.random,
        initialBalance = INITIAL_CALCULATOR_TEXT,
        currency = currency,
        isCreateAccountButtonEnabled = false,
      )
  }
}