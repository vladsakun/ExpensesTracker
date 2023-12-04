package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.InitialBalanceKeyboardActions
import kotlinx.coroutines.flow.StateFlow

@Stable
interface BottomSheetType {
  data class Balance(
    val text: StateFlow<String>,
    val actions: InitialBalanceKeyboardActions,
    val numericKeyboardActions: NumericKeyboardActions,
    val equalButtonState: StateFlow<EqualButtonState>,
    val decimalSeparator: String,
    val currency: String,
  ) : BottomSheetType
}