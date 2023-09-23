package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.resources.models.CalculatorTransactionUiModel
import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.asEntity
import com.emendo.expensestracker.core.data.model.asExternalModel
import com.emendo.expensestracker.core.data.model.asTransactionUiModel
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineFirstAccountsRepository @Inject constructor(
  private val accountsDao: AccountDao,
  private val transactionDao: TransactionDao,
  private val amountFormatter: AmountFormatter,
) : AccountsRepository {

  override fun getAccounts(): Flow<List<AccountModel>> {
    return accountsDao.getAll().map { accountEntities ->
      accountEntities.map { it.asExternalModel(amountFormatter) }
    }
  }

  override fun getLastUsedAccount(): Flow<AccountModel?> {
    return transactionDao.getAll().flatMapLatest { transactions ->
      val sourceId = transactions.lastOrNull()?.sourceId ?: return@flatMapLatest getFirstAccount()
      accountsDao.getById(sourceId).map { it.asExternalModel(amountFormatter) }
    }
  }

  // Todo Move to UseCase
  override fun getLastUsedTransactionSourceUiModel(): Flow<CalculatorTransactionUiModel?> {
    return getLastUsedAccount().map { it?.asTransactionUiModel() }
  }

  override suspend fun upsertAccount(accountModel: AccountModel) {
    withContext(Dispatchers.IO) {
      accountsDao.save(accountModel.asEntity())
    }
  }

  override suspend fun deleteAccount(accountModel: AccountModel) {
    withContext(Dispatchers.IO) {
      accountsDao.delete(accountModel.asEntity())
    }
  }

  // Todo Move to UseCase
  private fun getFirstAccount(): Flow<AccountModel?> =
    accountsDao.getAll().map { it.firstOrNull()?.asExternalModel(amountFormatter) }
}