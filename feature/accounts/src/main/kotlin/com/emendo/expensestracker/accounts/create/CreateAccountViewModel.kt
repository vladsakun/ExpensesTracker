package com.emendo.expensestracker.accounts.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.common.AccountBalanceUtils
import com.emendo.expensestracker.accounts.common.AccountScreenNavigator
import com.emendo.expensestracker.accounts.common.bottomsheet.AccountBottomSheetContract
import com.emendo.expensestracker.accounts.common.bottomsheet.AccountBottomSheetDelegate
import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.accounts.common.state.AccountStateManagerDelegate
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_ACCOUNT_BALANCE_BOTTOM_SHEET
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.data.api.manager.CurrencyCacheManager
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.model.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
  currencyCacheManager: CurrencyCacheManager,
  override val appNavigationEventBus: AppNavigationEventBus,
  override val numericKeyboardCommander: NumericKeyboardCommander,
  override val calculatorFormatter: CalculatorFormatter,
  override val amountFormatter: AmountFormatter,
  private val accountRepository: AccountRepository,
) : ViewModel(),
    AccountStateManager<Boolean> by AccountStateManagerDelegate(
      defaultState = UiState.Data(getDefaultCreateAccountScreenData(amountFormatter, currencyCacheManager))
    ),
    ModalBottomSheetStateManager by AccountBottomSheetDelegate(numericKeyboardCommander),
    AccountScreenNavigator,
    AccountBottomSheetContract,
    AccountBalanceUtils {

  override val modalBottomSheetStateManager: ModalBottomSheetStateManager
    get() = this
  override val accountStateManager: AccountStateManager<*>
    get() = this

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
      with(requireDataValue()) {
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
}

private fun getDefaultCreateAccountScreenData(
  amountFormatter: AmountFormatter,
  currencyCacheManager: CurrencyCacheManager,
): CreateAccountScreenData {
  val currency = currencyCacheManager.getGeneralCurrencySnapshot()
  return getDefaultCreateAccountState(
    currency = currency,
    balance = amountFormatter.format(BigDecimal.ZERO, currency),
  )
}