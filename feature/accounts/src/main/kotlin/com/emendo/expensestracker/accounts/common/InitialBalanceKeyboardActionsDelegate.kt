package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.app.base.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions

class InitialBalanceKeyboardActionsDelegate(
  private val numericKeyboardCommander: NumericKeyboardCommander,
) : InitialBalanceKeyboardActions {

  override fun onChangeSignClick() {
    numericKeyboardCommander.negate()
  }
}