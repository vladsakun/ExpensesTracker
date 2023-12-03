package com.emendo.expensestracker.core.ui.bottomsheet.base

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.model.data.keyboard.NumericKeyboardActions
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.InitialBalanceKeyboardActions
import kotlinx.collections.immutable.ImmutableList
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
    val currencies: ImmutableList<CurrencyModel>,
  ) : BottomSheetType

  data class Balance(
    val text: StateFlow<String>,
    val actions: InitialBalanceKeyboardActions,
    val numericKeyboardActions: NumericKeyboardActions,
    val equalButtonState: StateFlow<EqualButtonState>,
    val decimalSeparator: String,
    val currency: String,
  ) : BottomSheetType
}