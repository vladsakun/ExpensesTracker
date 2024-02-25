package com.emendo.expensestracker.accounts.common.bottomsheet

import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.ui.bottomsheet.BalanceBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate

class AccountBottomSheetDelegate(
  private val numericKeyboardCommander: NumericKeyboardCommander,
) : ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate() {

  override fun onDismissModalBottomSheet() {
    if (modalBottomSheetState.value.bottomSheetData is BalanceBottomSheetData) {
      numericKeyboardCommander.onDoneClick()
    }
  }
}