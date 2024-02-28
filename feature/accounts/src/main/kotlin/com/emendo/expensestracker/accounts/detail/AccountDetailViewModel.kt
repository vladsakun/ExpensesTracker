package com.emendo.expensestracker.accounts.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.accounts.common.AccountBalanceUtils
import com.emendo.expensestracker.accounts.common.AccountScreenNavigator
import com.emendo.expensestracker.accounts.common.bottomsheet.AccountBottomSheetContract
import com.emendo.expensestracker.accounts.common.bottomsheet.AccountBottomSheetDelegate
import com.emendo.expensestracker.accounts.common.state.AccountStateManager
import com.emendo.expensestracker.accounts.common.state.AccountStateManagerDelegate
import com.emendo.expensestracker.accounts.destinations.AccountDetailScreenDestination
import com.emendo.expensestracker.app.base.api.AppNavigationEventBus
import com.emendo.expensestracker.app.base.api.helper.NumericKeyboardCommander
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.domain.account.GetAccountSnapshotByIdUseCase
import com.emendo.expensestracker.core.ui.bottomsheet.base.ModalBottomSheetStateManager
import com.emendo.expensestracker.core.ui.bottomsheet.general.Action
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.data.api.amount.AmountFormatter
import com.emendo.expensestracker.data.api.amount.CalculatorFormatter
import com.emendo.expensestracker.data.api.repository.AccountRepository
import com.emendo.expensestracker.model.ui.UiState
import com.emendo.expensestracker.model.ui.dataValue
import com.emendo.expensestracker.model.ui.resourceValueOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ACCOUNT_DETAIL_DELETE_ACCOUNT_DIALOG = "account_detail_delete_account_dialog"

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  getAccountByIdUseCase: GetAccountSnapshotByIdUseCase,
  override val appNavigationEventBus: AppNavigationEventBus,
  override val numericKeyboardCommander: NumericKeyboardCommander,
  override val calculatorFormatter: CalculatorFormatter,
  override val amountFormatter: AmountFormatter,
  private val accountRepository: AccountRepository,
) : ViewModel(),
    AccountStateManager<Boolean> by AccountStateManagerDelegate(),
    ModalBottomSheetStateManager by AccountBottomSheetDelegate(numericKeyboardCommander),
    AccountScreenNavigator,
    AccountBottomSheetContract,
    AccountBalanceUtils {

  override val accountStateManager: AccountStateManager<Boolean>
    get() = this
  override val modalBottomSheetStateManager: ModalBottomSheetStateManager
    get() = this

  private val accountId: Long = savedStateHandle.getAccountId()
  private var editAccountJob: Job? = null

  init {
    if (state.value.dataValue() == null) {
      viewModelScope.launch {
        val account = getAccountByIdUseCase(savedStateHandle.getAccountId())
          .map(::getDefaultAccountDetailScreenState)
          .first()

        _state.update { UiState.Data(account) }
      }
    }
  }

  fun updateAccount() {
    if (editAccountJob != null) {
      return
    }

    editAccountJob = viewModelScope.launch {
      with(requireDataValue()) {
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
      GeneralBottomSheetData
        .Builder(
          id = ACCOUNT_DETAIL_DELETE_ACCOUNT_DIALOG,
          positiveAction = Action(resourceValueOf(R.string.delete), ::deleteAccount)
        )
        .title(resourceValueOf(R.string.account_detail_dialog_delete_confirm_title))
        .negativeAction(Action(resourceValueOf(R.string.cancel), ::hideModalBottomSheet))
        .build()
    )
  }

  private fun deleteAccount() {
    viewModelScope.launch {
      accountRepository.deleteAccount(accountId)
      navigateUp()
    }
  }
}

private fun SavedStateHandle.getAccountId(): Long =
  requireNotNull(this[AccountDetailScreenDestination.arguments[0].name])
