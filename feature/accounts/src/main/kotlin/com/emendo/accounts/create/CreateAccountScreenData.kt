package com.emendo.accounts.create

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.EqualButtonState

@Immutable
data class CreateAccountScreenData(
  val accountName: String,
  val icon: AccountIconModel,
  val color: ColorModel,
  val initialBalance: String,
  val currency: CurrencyModel,
  @Stable val equalButtonState: EqualButtonState,
  val decimalSeparator: String,
  val isCreateAccountButtonEnabled: Boolean,
) {

  companion object {
    fun getDefaultState(
      defaultCurrency: CurrencyModel = CurrencyModel.USD,
      decimalSeparator: String = ".",
    ) = CreateAccountScreenData(
      accountName = "",
      icon = AccountIconModel.GROCERIES,
      color = ColorModel.GREEN,
      initialBalance = "0",
      currency = defaultCurrency,
      equalButtonState = EqualButtonState.Done,
      decimalSeparator = decimalSeparator,
      isCreateAccountButtonEnabled = false,
    )
  }
}