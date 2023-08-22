package com.emendo.accounts.create

import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel

sealed interface BottomSheetType {
  data class Icon(
    val selectedIcon: AccountIconModel,
    val onSelectIcon: (icon: AccountIconModel) -> Unit
  ) : BottomSheetType

  data class Color(
    val selectedColor: ColorModel,
    val onSelectColor: (color: ColorModel) -> Unit
  ) : BottomSheetType

  data class Currency(
    val selectedCurrency: CurrencyModel,
    val onSelectCurrency: (currencyModel: CurrencyModel) -> Unit
  ) : BottomSheetType
}