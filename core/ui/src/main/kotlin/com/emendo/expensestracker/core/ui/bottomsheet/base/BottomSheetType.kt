package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.CurrencyModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.CalculatorKeyboardActions
import com.emendo.expensestracker.core.model.data.EqualButtonState
import com.emendo.expensestracker.core.model.data.InitialBalanceKeyboardActions
import kotlinx.coroutines.flow.StateFlow

@Stable
interface BottomSheetType {
  data class Icon(
    val selectedIcon: IconModel,
    val onSelectIcon: (icon: IconModel) -> Unit,
  ) : BottomSheetType

  data class Color(
    val selectedColor: ColorModel,
    val onSelectColor: (color: ColorModel) -> Unit,
  ) : BottomSheetType

  data class Currency(
    val selectedCurrency: CurrencyModel,
    val onSelectCurrency: (currencyModel: CurrencyModel) -> Unit,
  ) : BottomSheetType

  data class InitialBalance(
    val text: StateFlow<String>,
    val actions: InitialBalanceKeyboardActions,
    val equalButtonState: StateFlow<EqualButtonState>,
    val decimalSeparator: String,
    val currency: String,
  ) : BottomSheetType

  data class Calculator(
    val text: StateFlow<String>,
    val actions: CalculatorKeyboardActions,
    val equalButtonState: StateFlow<EqualButtonState>,
    val decimalSeparator: String,
    val currencyState: StateFlow<String>,
    val source: StateFlow<CalculatorTransactionUiModel?>,
    val target: StateFlow<CalculatorTransactionUiModel?>,
  ) : BottomSheetType
}