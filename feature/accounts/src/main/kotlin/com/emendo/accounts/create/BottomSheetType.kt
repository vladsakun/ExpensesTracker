package com.emendo.accounts.create

import android.os.Parcelable
import androidx.compose.runtime.State
import com.emendo.expensestracker.core.data.model.AccountIconModel
import com.emendo.expensestracker.core.data.model.ColorModel
import com.emendo.expensestracker.core.data.model.CurrencyModel
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.EqualButtonState
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.NumKeyboardActions
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed interface BottomSheetType : Parcelable {

  @Parcelize
  data class Icon(
    val selectedIcon: AccountIconModel,
    val onSelectIcon: (icon: AccountIconModel) -> Unit,
  ) : BottomSheetType

  @Parcelize
  data class Color(
    val selectedColor: ColorModel,
    val onSelectColor: (color: ColorModel) -> Unit,
  ) : BottomSheetType

  @Parcelize
  data class Currency(
    val selectedCurrency: CurrencyModel,
    val onSelectCurrency: (currencyModel: CurrencyModel) -> Unit,
  ) : BottomSheetType

  @Parcelize
  data class Calculator(
    val initialBalanceActions: NumKeyboardActions.InitialBalanceActions,
    val decimalSeparator: String,
  ) : BottomSheetType {

    @IgnoredOnParcel
    var textState: State<String>? = null

    @IgnoredOnParcel
    var currencyState: State<String>? = null

    @IgnoredOnParcel
    var equalButtonState: State<EqualButtonState>? = null
  }

  @Parcelize
  data object Initial : BottomSheetType
}