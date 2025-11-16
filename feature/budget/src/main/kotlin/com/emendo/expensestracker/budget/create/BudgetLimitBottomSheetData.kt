package com.emendo.expensestracker.budget.create

import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.InitialBalanceKeyboardActions
import com.emendo.expensestracker.model.ui.NumericKeyboardActions
import kotlinx.coroutines.flow.StateFlow

data class BudgetLimitBottomSheetData(
  val value: StateFlow<String>,
  val onValueChanged: (String) -> Unit,
  val actions: InitialBalanceKeyboardActions,
  val equalButtonState: StateFlow<EqualButtonState>,
  val decimalSeparator: String,
  val currency: String,
  val numericKeyboardActions: NumericKeyboardActions,
) : BottomSheetData