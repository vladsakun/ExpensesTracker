package com.emendo.expensestracker.createtransaction.transaction

import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.NumericKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorBottomSheetState
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorKeyboardActions
import kotlinx.coroutines.flow.StateFlow

data class CalculatorBottomSheetData(
  val state: StateFlow<CalculatorBottomSheetState>,
  val actions: CalculatorKeyboardActions,
  val numericKeyboardActions: NumericKeyboardActions,
  val decimalSeparator: String,
) : BottomSheetData