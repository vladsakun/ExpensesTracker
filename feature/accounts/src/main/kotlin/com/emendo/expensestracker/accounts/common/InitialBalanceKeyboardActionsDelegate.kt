package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions

class InitialBalanceKeyboardActionsDelegate(
  private val numericKeyboardCommander: com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander,
) : InitialBalanceKeyboardActions {

  override fun onChangeSignClick() {
    numericKeyboardCommander.negate()
  }
}