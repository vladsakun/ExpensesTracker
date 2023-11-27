package com.emendo.expensestracker.accounts.create

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.repository.api.AccountsRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.model.data.keyboard.EqualButtonState
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseBottomSheetViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.numkeyboard.InitialBalanceKeyboardActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  private val accountsRepository: AccountsRepository,
  private val calculatorFormatter: CalculatorFormatter,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val currencyCacheManager: CurrencyCacheManager,
  private val numericKeyboardCommander: NumericKeyboardCommander,
) : BaseBottomSheetViewModel<BottomSheetType>(), InitialBalanceKeyboardActions {

  private val _state = MutableStateFlow(
    CreateAccountScreenData.getDefaultState(
      currency = currencyCacheManager.getGeneralCurrencySnapshot()
    )
  )

  val state = _state.asStateFlow()

  private var createAccountJob: Job? = null

  init {
    numericKeyboardCommander.setCallbacks(doneClick = ::doneClick, valueChanged = ::updateValue)

    //    if (IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET) {
    //      viewModelScope.launch {
    //        delay(100)
    //        showBottomSheet(
    //          BottomSheetType.InitialBalance(
    //            text = initialBalanceState.asStateFlow(),
    //            actions = this@CreateAccountViewModel,
    //            decimalSeparator = amountFormatter.decimalSeparator.toString(),
    //            equalButtonState = equalButtonState.asStateFlow(),
    //            currency = state.value.currency.currencyName,
    //          )
    //        )
    //      }
    //    }
  }

  private fun updateValue(formattedValue: String, equalButtonState: EqualButtonState): Boolean {
    _state.update { it.copy(initialBalance = formattedValue) }
    return false
  }

  override fun onChangeSignClick() {
    // Todo
  }

  private fun doneClick(): Boolean {
    hideBottomSheet()
    return false
  }

  override fun dismissBottomSheet() {
    if (bottomSheetState.value.bottomSheetState is BottomSheetType.InitialBalance) {
      numericKeyboardCommander.onDoneClick()
    }
    super.dismissBottomSheet()
  }

  fun createNewAccount() {
    if (createAccountJob != null) {
      return
    }

    createAccountJob = viewModelScope.launch(ioDispatcher) {
      with(state.value) {
        accountsRepository.createAccount(
          name = accountName,
          balance = calculatorFormatter.toBigDecimal(initialBalance),
          currency = currency,
          icon = icon,
          color = color,
        )
        navigateUp()
      }
    }
  }

  fun onIconRowClick() {
    showBottomSheet(
      BottomSheetType.Icon(
        selectedIcon = state.value.icon,
        onSelectIcon = ::setIcon,
      )
    )
  }

  fun onColorRowClick() {
    showBottomSheet(
      BottomSheetType.Color(
        selectedColor = state.value.color,
        onSelectColor = ::setColor,
      )
    )
  }

  fun onCurrencyRowClick() {
    showBottomSheet(
      BottomSheetType.Currency(
        selectedCurrency = state.value.currency,
        onSelectCurrency = ::setCurrency,
        currencies = currencyCacheManager.getCurrenciesBlocking().values.toImmutableList(),
      )
    )
  }

  fun setAccountName(accountName: String) {
    _state.update {
      it.copy(
        accountName = accountName,
        isCreateAccountButtonEnabled = accountName.isNotBlank(),
      )
    }
  }

  fun onInitialBalanceClick() {
    showBottomSheet(
      BottomSheetType.InitialBalance(
        text = numericKeyboardCommander.calculatorTextState,
        actions = this,
        decimalSeparator = calculatorFormatter.decimalSeparator.toString(),
        equalButtonState = numericKeyboardCommander.equalButtonState,
        currency = state.value.currency.currencyName,
        numericKeyboardActions = numericKeyboardCommander,
      )
    )
  }

  private fun setCurrency(currency: CurrencyModel) {
    _state.update { it.copy(currency = currency) }
  }

  private fun setIcon(icon: IconModel) {
    _state.update { it.copy(icon = icon) }
  }

  private fun setColor(color: ColorModel) {
    _state.update { it.copy(color = color) }
  }
}