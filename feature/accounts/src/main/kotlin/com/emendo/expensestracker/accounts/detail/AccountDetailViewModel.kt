package com.emendo.expensestracker.accounts.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.common.AccountViewModel
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.amount.CalculatorFormatter
import com.emendo.expensestracker.core.data.helper.NumericKeyboardCommander
import com.emendo.expensestracker.core.data.mapper.CurrencyMapper
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.domain.GetAccountSnapshotById
import com.emendo.expensestracker.core.model.data.CurrencyModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.Action
import com.emendo.expensestracker.core.ui.bottomsheet.base.GeneralBottomSheetData
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
  savedStateHandle: SavedStateHandle,
  getAccountSnapshotById: GetAccountSnapshotById,
  numericKeyboardCommander: NumericKeyboardCommander,
  private val amountFormatter: AmountFormatter,
  private val accountRepository: AccountRepository,
  private val calculatorFormatter: CalculatorFormatter,
  currencyMapper: CurrencyMapper,
) : AccountViewModel(calculatorFormatter, numericKeyboardCommander, amountFormatter, currencyMapper) {

  private val accountId: Long = requireNotNull(savedStateHandle[AccountDetailScreenDestination.arguments[0].name])

  private val _state: MutableStateFlow<AccountDetailScreenData> = MutableStateFlow(
    AccountDetailScreenData.getDefaultState(requireNotNull(getAccountSnapshotById(accountId)))
  )
  override val state: StateFlow<AccountDetailScreenData> = _state.asStateFlow()

  private var editAccountJob: Job? = null

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
          balance = calculatorFormatter.toBigDecimal(amountFormatter.removeCurrency(balance, currency)),
          currency = currency,
          icon = icon,
          color = color,
        )
        navigateUp()
      }
    }
  }

  fun showConfirmDeleteAccountBottomSheet() {
    showBottomSheet(
      GeneralBottomSheetData.Builder(Action(TextValue.Resource(R.string.delete), ::deleteAccount))
        .title(TextValue.Resource(R.string.account_detail_dialog_delete_confirm_title))
        .negativeAction(Action(TextValue.Resource(R.string.cancel), ::hideBottomSheet))
        .create()
    )
  }

  private fun deleteAccount() {
    viewModelScope.launch {
      accountRepository.deleteAccount(accountId)
      navigateUp()
    }
  }
}