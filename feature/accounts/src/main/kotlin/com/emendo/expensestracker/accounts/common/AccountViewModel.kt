package com.emendo.expensestracker.accounts.common

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.accounts.common.navigator.AccountScreenNavigator
import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.core.app.base.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.ui.bottomsheet.BalanceBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetStateManagerDelegate

// Todo use composition over inheritance
abstract class AccountViewModel(
  private val calculatorFormatter: CalculatorFormatter,
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
) : ViewModel(),
    BottomSheetStateManager by BottomSheetStateManagerDelegate(),
    InitialBalanceKeyboardActions by InitialBalanceKeyboardActionsDelegate(numericKeyboardCommander),
    AccountStateManager,
    AccountScreenNavigator {

  override val accountStateManager: AccountStateManager
    get() = this

  init {
    numericKeyboardCommander.setCallbacks(doneClick = ::doneClick, onMathDone = ::updateValue)
  }

  override fun onDismissBottomSheet() {
    if (bottomSheetState.value.bottomSheetState is BalanceBottomSheetData) {
      numericKeyboardCommander.onDoneClick()
    }
  }

  fun showBalanceBottomSheet() {
    showBottomSheet(
      BalanceBottomSheetData(
        text = numericKeyboardCommander.calculatorTextState,
        actions = this,
        decimalSeparator = calculatorFormatter.decimalSeparator.toString(),
        equalButtonState = numericKeyboardCommander.equalButtonState,
        currency = state.value.currency.currencyName,
        numericKeyboardActions = numericKeyboardCommander,
      )
    )
  }

  private fun updateValue(value: String): Boolean {
    val formattedValue = amountFormatter.format(calculatorFormatter.toBigDecimal(value), state.value.currency)
    updateBalance(formattedValue)
    return false
  }

  private fun doneClick(): Boolean {
    hideBottomSheet()
    return false
  }
}