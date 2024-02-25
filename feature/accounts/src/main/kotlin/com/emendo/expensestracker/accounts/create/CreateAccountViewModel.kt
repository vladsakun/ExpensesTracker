package com.emendo.expensestracker.accounts.create

import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.common.AccountViewModel
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.data.api.manager.CurrencyCacheManager
import com.emendo.expensestracker.data.api.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  currencyCacheManager: CurrencyCacheManager,
  numericKeyboardCommander: NumericKeyboardCommander,
  calculatorFormatter: CalculatorFormatter,
  private val amountFormatter: AmountFormatter,
  private val accountRepository: AccountRepository,
  override val appNavigationEventBus: AppNavigationEventBus,
) : AccountViewModel(calculatorFormatter, numericKeyboardCommander, amountFormatter) {

  private val _state: MutableStateFlow<CreateAccountScreenData> = MutableStateFlow(
    getDefaultCreateAccountScreenData(currencyCacheManager)
  )

  private fun getDefaultCreateAccountScreenData(currencyCacheManager: CurrencyCacheManager): CreateAccountScreenData {
    val currency = currencyCacheManager.getGeneralCurrencySnapshot()
    return CreateAccountScreenData.getDefaultState(
      currency = currency,
      balance = amountFormatter.format(BigDecimal.ZERO, currency),
    )
  }

  override val state: StateFlow<CreateAccountScreenData> = _state.asStateFlow()

  private var createAccountJob: Job? = null

  init {
    if (IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET) {
      viewModelScope.launch {
        delay(100)
        showBalanceBottomSheet()
      }
    }
  }

  override fun updateBalance(balance: Amount) {
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

  fun createNewAccount() {
    if (createAccountJob != null) {
      return
    }

    createAccountJob = viewModelScope.launch {
      with(state.value) {
        accountRepository.createAccount(
          name = name,
          balance = balance.value,
          currency = currency,
          icon = icon,
          color = color,
        )
        navigateUp()
      }
    }
  }

  fun updateCurrencyByCode(code: String) {
    updateCurrencyByCode(amountFormatter, code)
  }
}