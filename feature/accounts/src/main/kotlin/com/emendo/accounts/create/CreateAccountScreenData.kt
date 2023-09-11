package com.emendo.accounts.create

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.EqualButtonState
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

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
  val hideBottomSheetEvent: StateEvent = consumed,
  val navigateUpEvent: StateEvent = consumed,
) {

  companion object {
    const val DEFAULT_INITIAL_BALANCE = "0"

    fun getDefaultState(
      defaultCurrency: CurrencyModel = CurrencyModel.USD,  // Todo pass favourite currency
      decimalSeparator: String = ".",
    ) = CreateAccountScreenData(
      accountName = "",
      icon = IconModel.random,
      color = ColorModel.random,
      initialBalance = DEFAULT_INITIAL_BALANCE,
      currency = defaultCurrency,
      equalButtonState = EqualButtonState.Default,
      decimalSeparator = decimalSeparator,
      isCreateAccountButtonEnabled = false,
    )
  }
}