package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.data.model.AccountModel
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
  fun getAccounts(): Flow<List<AccountModel>>
  fun getById(id: Long): Flow<AccountModel>

  suspend fun upsertAccount(accountModel: AccountModel)
  suspend fun deleteAccount(accountModel: AccountModel)
}