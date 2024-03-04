package com.emendo.expensestracker.createtransaction.transaction.domain

import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorBottomSheetState
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.CalculatorKeyboardActions
import com.emendo.expensestracker.createtransaction.transaction.data.CalculatorBottomSheetData
import com.emendo.expensestracker.data.api.DecimalSeparator
import com.emendo.expensestracker.model.ui.NumericKeyboardActions
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCalculatorBottomSheetDataUseCase @Inject constructor(
  @DecimalSeparator private val decimalSeparator: String,
) {

  operator fun invoke(
    calculatorState: StateFlow<CalculatorBottomSheetState>,
    actions: CalculatorKeyboardActions,
    numericKeyboardActions: NumericKeyboardActions,
  ) = CalculatorBottomSheetData(
    state = calculatorState,
    actions = actions,
    numericKeyboardActions = numericKeyboardActions,
    decimalSeparator = decimalSeparator,
  )
}