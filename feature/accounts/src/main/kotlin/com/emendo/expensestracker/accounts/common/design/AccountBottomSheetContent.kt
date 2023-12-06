package com.emendo.expensestracker.accounts.common.design

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.ui.bottomsheet.BalanceBottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.NumericKeyboardBottomSheet

@Composable
internal fun AccountBottomSheetContent(
  type: BottomSheetData?,
) {
  when (type) {
    is BalanceBottomSheetData -> {
      val text = type.text.collectAsStateWithLifecycle()
      val equalButtonState = type.equalButtonState.collectAsStateWithLifecycle()

      NumericKeyboardBottomSheet(
        textProvider = text::value,
        actions = type.actions,
        equalButtonStateProvider = equalButtonState::value,
        decimalSeparator = type.decimalSeparator,
        currency = type.currency,
        numericKeyboardActions = type.numericKeyboardActions,
      )
    }
  }
}