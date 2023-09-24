package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.data.model.AccountModel
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
  fun getAccounts(): Flow<List<AccountModel>>
  fun getLastUsedAccount(): Flow<AccountModel?>
  fun getLastUsedTransactionSourceUiModel(): Flow<CalculatorTransactionUiModel?>
  suspend fun upsertAccount(accountModel: AccountModel)
  suspend fun deleteAccount(accountModel: AccountModel)
}