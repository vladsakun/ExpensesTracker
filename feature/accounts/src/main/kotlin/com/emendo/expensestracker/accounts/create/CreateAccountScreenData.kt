package com.emendo.expensestracker.accounts.create

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.DEFAULT_CALCULATOR_NUM_1
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.EqualButtonState

@Stable
data class CreateAccountScreenData(
  val accountName: String,
  val icon: IconModel,
  val color: ColorModel,
  val initialBalance: String,
  val currency: CurrencyModel,
  val equalButtonState: EqualButtonState,
  val isCreateAccountButtonEnabled: Boolean,
) {

  companion object {
    fun getDefaultState(currency: CurrencyModel) =
      CreateAccountScreenData(
        accountName = "",
        icon = IconModel.random,
        color = ColorModel.random,
        initialBalance = DEFAULT_CALCULATOR_NUM_1,
        currency = currency,
        equalButtonState = EqualButtonState.Default,
        isCreateAccountButtonEnabled = false,
      )
  }
}