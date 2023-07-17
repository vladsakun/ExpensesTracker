package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.model.Account
import com.emendo.expensestracker.core.data.model.toExternalModel
import com.emendo.expensestracker.core.data.model.asEntity
import com.emendo.expensestracker.core.database.dao.AccountDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstAccountsRepository @Inject constructor(
  private val accountsDao: AccountDao,
) : AccountsRepository {

  override fun getAccounts(): Flow<List<Account>> {
    return accountsDao.getAll().map { accountEntities ->
      accountEntities.map { it.toExternalModel() }
    }
  }

  override suspend fun upsertAccount(account: Account) {
    withContext(Dispatchers.IO) {
      accountsDao.save(account.asEntity())
    }
  }

  override suspend fun deleteAccount(account: Account) {
    withContext(Dispatchers.IO) {
      accountsDao.delete(account.asEntity())
    }
  }
}