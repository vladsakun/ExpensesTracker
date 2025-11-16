package com.emendo.expensestracker.core.ui.bottomsheet

import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.model.ui.NumericKeyboardActions
import kotlinx.coroutines.flow.StateFlow

data class BalanceBottomSheetData constructor(
  val text: StateFlow<String>,
  val actions: InitialBalanceKeyboardActions,
  val numericKeyboardActions: NumericKeyboardActions,
  val equalButtonState: StateFlow<EqualButtonState>,
  val decimalSeparator: String,
  val currency: String,
) : BottomSheetData
