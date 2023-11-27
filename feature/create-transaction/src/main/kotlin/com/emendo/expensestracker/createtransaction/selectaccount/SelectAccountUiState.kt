package com.emendo.expensestracker.createtransaction.selectaccount

import com.emendo.expensestracker.core.data.model.AccountModel
import kotlinx.collections.immutable.ImmutableList

sealed interface SelectAccountUiState {
  data object Loading : SelectAccountUiState
  data object Default : SelectAccountUiState
  data class Error(val message: String) : SelectAccountUiState
  data object Empty : SelectAccountUiState
  data class DisplayAccountsList(val accountModels: ImmutableList<AccountModel>) : SelectAccountUiState
}