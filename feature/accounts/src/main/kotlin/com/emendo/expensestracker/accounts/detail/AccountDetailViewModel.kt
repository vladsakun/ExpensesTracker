package com.emendo.expensestracker.accounts.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.common.AccountViewModel
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.core.app.base.eventbus.AppNavigationEventBus
import com.emendo.expensestracker.core.app.base.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.resourceValueOf
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.domain.account.GetAccountSnapshotByIdUseCase
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
  override val appNavigationEventBus: AppNavigationEventBus,
  savedStateHandle: SavedStateHandle,
  getAccountSnapshotByIdUseCase: GetAccountSnapshotByIdUseCase,
  numericKeyboardCommander: NumericKeyboardCommander,
  calculatorFormatter: CalculatorFormatter,
  private val amountFormatter: AmountFormatter,
  private val accountRepository: AccountRepository,
) : AccountViewModel(calculatorFormatter, numericKeyboardCommander, amountFormatter) {

  private val accountId: Long = requireNotNull(savedStateHandle[AccountDetailScreenDestination.arguments[0].name])

  private val _state: MutableStateFlow<AccountDetailScreenData> = MutableStateFlow(
    AccountDetailScreenData.getDefaultState(requireNotNull(getAccountSnapshotByIdUseCase(accountId)))
  )
  override val state: StateFlow<AccountDetailScreenData> = _state.asStateFlow()

  private var editAccountJob: Job? = null

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
    _state.update { it.copy(isConfirmAccountDetailsButtonEnabled = enabled) }
  }

  fun updateAccount() {
    if (editAccountJob != null) {
      return
    }

    editAccountJob = viewModelScope.launch {
      with(state.value) {
        accountRepository.updateAccount(
          id = accountId,
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

  fun showConfirmDeleteAccountBottomSheet() {
    showModalBottomSheet(
      GeneralBottomSheetData.Builder(Action(resourceValueOf(R.string.delete), ::deleteAccount))
        .title(resourceValueOf(R.string.account_detail_dialog_delete_confirm_title))
        .negativeAction(Action(resourceValueOf(R.string.cancel), ::hideModalBottomSheet))
        .build()
    )
  }

  fun updateCurrencyByCode(code: String) {
    updateCurrencyByCode(amountFormatter, code)
  }

  private fun deleteAccount() {
    viewModelScope.launch {
      accountRepository.deleteAccount(accountId)
      navigateUp()
    }
  }
}