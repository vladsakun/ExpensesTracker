package com.emendo.expensestracker.core.data.helper

import com.emendo.expensestracker.core.model.data.BottomSheetData
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.InitialBalanceKeyboardActions
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions
import kotlinx.coroutines.flow.StateFlow

interface BalanceBottomSheetData : BottomSheetData {
  val text: StateFlow<String>
  val actions: InitialBalanceKeyboardActions
  val numericKeyboardActions: NumericKeyboardActions
  val equalButtonState: StateFlow<EqualButtonState>
  val decimalSeparator: String
  val currency: String
}

data class BalanceBottomSheetDataImpl(
  override val text: StateFlow<String>,
  override val actions: InitialBalanceKeyboardActions,
  override val numericKeyboardActions: NumericKeyboardActions,
  override val equalButtonState: StateFlow<EqualButtonState>,
  override val decimalSeparator: String,
  override val currency: String,
) : BalanceBottomSheetData
