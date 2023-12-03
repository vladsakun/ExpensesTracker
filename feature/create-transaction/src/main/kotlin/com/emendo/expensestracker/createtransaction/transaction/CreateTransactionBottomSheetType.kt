package com.emendo.expensestracker.createtransaction.transaction

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorBottomSheetState
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorKeyboardActions
import kotlinx.coroutines.flow.StateFlow

@Stable
interface CreateTransactionBottomSheetType

data class CalculatorBottomSheet(
  val state: StateFlow<CalculatorBottomSheetState>,
  val actions: CalculatorKeyboardActions,
  val numericKeyboardActions: NumericKeyboardActions,
  val decimalSeparator: String,
) : CreateTransactionBottomSheetType