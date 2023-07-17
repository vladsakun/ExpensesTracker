package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
  fun getAccounts(): Flow<List<Account>>
  suspend fun upsertAccount(account: Account)
  suspend fun deleteAccount(account: Account)
}