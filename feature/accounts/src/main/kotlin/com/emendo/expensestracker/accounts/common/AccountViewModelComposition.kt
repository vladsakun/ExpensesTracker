package com.emendo.expensestracker.accounts.common

import androidx.lifecycle.ViewModel
import com.emendo.expensestracker.accounts.common.navigator.AccountScreenNavigator
import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.BalanceBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter

interface NumericKeyboardDelegate {

}

// Todo use composition over inheritance
abstract class AccountViewModelComposition(
  private val calculatorFormatter: CalculatorFormatter,
  private val numericKeyboardCommander: com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
) : ViewModel(),
    ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate(),
    InitialBalanceKeyboardActions by InitialBalanceKeyboardActionsDelegate(numericKeyboardCommander),
    AccountStateManager,
    AccountScreenNavigator {

  override val accountStateManager: AccountStateManager
    get() = this

  init {
    numericKeyboardCommander.setCallbacks(doneClick = ::doneClick, onMathDone = ::updateBalanceValue)
  }

  override fun onDismissModalBottomSheet() {
    if (modalBottomSheetState.value.bottomSheetData is BalanceBottomSheetData) {
      numericKeyboardCommander.onDoneClick()
    }
  }

  fun showBalanceBottomSheet() {
    showModalBottomSheet(
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

  private fun updateBalanceValue(value: String): Boolean {
    val formattedValue = amountFormatter.format(calculatorFormatter.toBigDecimal(value), state.value.currency)
    updateBalance(formattedValue)
    return false
  }

  private fun doneClick(): Boolean {
    hideModalBottomSheet()
    return false
  }
}