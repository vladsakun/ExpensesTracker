package com.emendo.accounts.create

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.DEFAULT_CALCULATOR_NUM_1
import com.emendo.expensestracker.core.model.data.EqualButtonState

@Stable
data class CreateAccountScreenData(
  val accountName: String,
  val icon: IconModel,
  val color: ColorModel,
  val initialBalance: String,
  val currency: CurrencyModel,
  val equalButtonState: EqualButtonState,
  val decimalSeparator: String,
  val isCreateAccountButtonEnabled: Boolean,
) {

  companion object {
    fun getDefaultState(
      decimalSeparator: String,
      defaultCurrency: CurrencyModel = CurrencyModel.USD,  // Todo pass favourite currency
    ) = CreateAccountScreenData(
      accountName = "",
      icon = IconModel.random,
      color = ColorModel.random,
      initialBalance = DEFAULT_CALCULATOR_NUM_1,
      currency = defaultCurrency,
      equalButtonState = EqualButtonState.Default,
      decimalSeparator = decimalSeparator,
      isCreateAccountButtonEnabled = false,
    )
  }
}