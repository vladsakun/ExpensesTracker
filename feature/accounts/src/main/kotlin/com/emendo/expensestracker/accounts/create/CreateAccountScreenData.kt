package com.emendo.expensestracker.accounts.create

import com.emendo.expensestracker.accounts.common.AccountScreenData
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.keyboard.CalculatorConstants.INITIAL_CALCULATOR_TEXT

data class CreateAccountScreenData(
  override val name: String,
  override val icon: IconModel,
  override val color: ColorModel,
  override val balance: String,
  override val currency: CurrencyModel,
  val isCreateAccountButtonEnabled: Boolean,
) : AccountScreenData {

  companion object {
    fun getDefaultState(currency: CurrencyModel) =
      CreateAccountScreenData(
        name = "",
        icon = IconModel.random,
        color = ColorModel.random,
        balance = INITIAL_CALCULATOR_TEXT,
        currency = currency,
        isCreateAccountButtonEnabled = false,
      )
  }
}