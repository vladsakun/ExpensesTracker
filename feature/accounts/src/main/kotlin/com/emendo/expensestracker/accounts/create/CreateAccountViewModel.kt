package com.emendo.expensestracker.accounts.create

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.common.AccountViewModel
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.data.manager.cache.CurrencyCacheManager
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.model.data.CurrencyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  currencyCacheManager: CurrencyCacheManager,
  numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
  private val accountRepository: AccountRepository,
  private val calculatorFormatter: CalculatorFormatter,
) : AccountViewModel(calculatorFormatter, currencyCacheManager, numericKeyboardCommander, amountFormatter) {

  private val _state: MutableStateFlow<CreateAccountScreenData> = MutableStateFlow(
    CreateAccountScreenData.getDefaultState(
      currency = currencyCacheManager.getGeneralCurrencySnapshot()
    )
  )

  override val state = _state.asStateFlow()

  private var createAccountJob: Job? = null

  init {
    if (IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET) {
      viewModelScope.launch {
        delay(100)
        showBalanceBottomSheet()
      }
    }
  }

  fun createNewAccount() {
    if (createAccountJob != null) {
      return
    }

    createAccountJob = viewModelScope.launch {
      with(state.value) {
        accountRepository.createAccount(
          name = name,
          balance = calculatorFormatter.toBigDecimal(amountFormatter.removeCurrency(balance, currency)),
          currency = currency,
          icon = icon,
          color = color,
        )
        navigateUp()
      }
    }
  }

  override fun updateBalance(balance: String) {
    _state.update { it.copy(balance = balance) }
  }

  override fun updateCurrency(currency: CurrencyModel) {
    _state.update { it.copy(currency = currency) }
  }

  override fun updateIcon(icon: IconModel) {
    _state.update { it.copy(icon = icon) }
  }

  override fun updateColor(color: ColorModel) {
    _state.update { it.copy(color = color) }
  }

  override fun updateName(name: String) {
    _state.update { it.copy(name = name) }
  }

  override fun updateConfirmEnabled(enabled: Boolean) {
    _state.update { it.copy(isCreateAccountButtonEnabled = enabled) }
  }
}