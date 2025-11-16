package com.emendo.expensestracker.budget.create

import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.ui.bottomsheet.BalanceBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManagerDelegate

class BudgetBottomSheetDelegate(
  private val numericKeyboardCommander: NumericKeyboardCommander,
) : ModalBottomSheetStateManager by ModalBottomSheetStateManagerDelegate() {

  override fun onDismissModalBottomSheet() {
    if (modalBottomSheetState.value.bottomSheetData is BalanceBottomSheetData) {
      numericKeyboardCommander.onDoneClick()
    }
  }
}

