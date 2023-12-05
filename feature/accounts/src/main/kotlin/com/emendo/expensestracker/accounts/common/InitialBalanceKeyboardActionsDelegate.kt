package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.model.data.keyboard.InitialBalanceKeyboardActions

class InitialBalanceKeyboardActionsDelegate(
  private val numericKeyboardCommander: NumericKeyboardCommander,
) : InitialBalanceKeyboardActions {

  override fun onChangeSignClick() {
    numericKeyboardCommander.negate()
  }
}