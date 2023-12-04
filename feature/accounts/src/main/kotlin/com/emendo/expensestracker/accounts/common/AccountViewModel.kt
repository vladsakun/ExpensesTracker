package com.emendo.expensestracker.accounts.common

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.data.mapper.CurrencyMapper
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.InitialBalanceKeyboardActions
import kotlinx.coroutines.flow.StateFlow

abstract class AccountViewModel(
  private val calculatorFormatter: CalculatorFormatter,
  private val numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
  private val currencyMapper: CurrencyMapper,
) : BaseBottomSheetViewModel<BottomSheetType>(), InitialBalanceKeyboardActions {

  abstract val state: StateFlow<AccountScreenData>

  abstract fun updateBalance(balance: String)
  abstract fun updateCurrency(currency: CurrencyModel)
  abstract fun updateIcon(icon: IconModel)
  abstract fun updateColor(color: ColorModel)
  abstract fun updateName(name: String)
  abstract fun updateConfirmEnabled(enabled: Boolean)

  val selectedColor: ColorModel
    get() = state.value.color

  init {
    numericKeyboardCommander.setCallbacks(doneClick = ::doneClick, onMathDone = ::updateValue)
  }

  override fun onChangeSignClick() {
    numericKeyboardCommander.negate()
  }

  override fun dismissBottomSheet() {
    if (bottomSheetState.value.bottomSheetState is BottomSheetType.Balance) {
      numericKeyboardCommander.onDoneClick()
    }
    super.dismissBottomSheet()
  }

  fun openIconBottomSheet() {
    showBottomSheet(
      BottomSheetType.Icon(
        selectedIcon = state.value.icon,
        onSelectIcon = ::setIcon,
      )
    )
  }

  fun openCurrencyBottomSheet() {
    //    showBottomSheet(
    //      BottomSheetType.Currency(
    //        selectedCurrency = state.value.currency,
    //        onSelectCurrency = ::setCurrency,
    //        currencies = currencyCacheManager.getCurrenciesBlocking().values.toImmutableList(),
    //      )
    //    )
  }

  fun setAccountName(accountName: String) {
    updateName(accountName)
    updateConfirmEnabled(state.value.name.isNotBlank())
  }

  fun showBalanceBottomSheet() {
    showBottomSheet(
      BottomSheetType.Balance(
        text = numericKeyboardCommander.calculatorTextState,
        actions = this,
        decimalSeparator = calculatorFormatter.decimalSeparator.toString(),
        equalButtonState = numericKeyboardCommander.equalButtonState,
        currency = state.value.currency.currencyName,
        numericKeyboardActions = numericKeyboardCommander,
      )
    )
  }

  fun updateColorById(id: Int) {
    updateColor(ColorModel.getById(id))
  }

  fun updateCurrencyByCode(code: String) {
    updateCurrency(currencyMapper.toCurrencyModelBlocking(code))
  }

  private fun updateValue(value: String): Boolean {
    val formattedValue = amountFormatter.format(calculatorFormatter.toBigDecimal(value), state.value.currency)
    updateBalance(formattedValue)
    return false
  }

  private fun doneClick(): Boolean {
    hideBottomSheet()
    return false
  }

  private fun setCurrency(currency: CurrencyModel) {
    val balance = amountFormatter.replaceCurrency(state.value.balance, state.value.currency, currency)
    updateBalance(balance)
    updateCurrency(currency)
  }

  private fun setIcon(icon: IconModel) {
    updateIcon(icon)
  }

  private fun setColor(color: ColorModel) {
    updateColor(color)
  }
}