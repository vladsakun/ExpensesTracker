package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.data.mapper.AccountMapper
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.asEntity
import com.emendo.expensestracker.core.data.repository.api.AccountsRepository
import com.emendo.expensestracker.core.database.dao.AccountDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstAccountsRepository @Inject constructor(
  private val accountsDao: AccountDao,
  private val accountMapper: AccountMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AccountsRepository {

  override fun getAccounts(): Flow<List<AccountModel>> =
    accountsDao.getAll().map { accountEntities ->
      accountEntities.map { accountMapper.map(it) }
    }

  override fun getById(id: Long): Flow<AccountModel> =
    accountsDao.getById(id).map(accountMapper::map)

  override suspend fun upsertAccount(accountModel: AccountModel) {
    withContext(ioDispatcher) {
      accountsDao.save(accountModel.asEntity())
    }
  }

  override suspend fun deleteAccount(accountModel: AccountModel) {
    withContext(ioDispatcher) {
      accountsDao.delete(accountModel.asEntity())
    }
  }
}