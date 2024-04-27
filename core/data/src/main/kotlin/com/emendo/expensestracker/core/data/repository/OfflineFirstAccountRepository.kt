package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInLazilyList
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.mapper.AccountMapper
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.model.AccountDetailUpdate
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.database.model.AccountOrdinalIndexUpdate
import com.emendo.expensestracker.core.database.util.DatabaseUtils
import com.emendo.expensestracker.core.model.data.AccountWithOrdinalIndex
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class OfflineFirstAccountRepository @Inject constructor(
  private val accountsDao: AccountDao,
  private val accountMapper: AccountMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @ApplicationScope private val scope: CoroutineScope,
  private val databaseUtils: DatabaseUtils,
) : AccountRepository {

  private val accountsList: StateFlow<List<AccountModel>> = accountsDao
    .getAll()
    .map { accounts -> accounts.map { accountMapper.map(it) } }
    .stateInLazilyList(scope)

  override fun getAccounts(): Flow<List<AccountModel>> = accountsList
  override fun getAccountsSnapshot(): List<AccountModel> = accountsList.value

  override fun getLastAccount(): Flow<AccountModel?> =
    accountsDao
      .getLastAccount()
      .map { account -> account?.let { accountMapper.map(it) } }

  override fun getById(id: Long): Flow<AccountModel> =
    accountsDao
      .getById(id)
      .map(accountMapper::map)

  override suspend fun createAccount(
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: com.emendo.expensestracker.model.ui.ColorModel,
    balance: BigDecimal,
  ) {
    withContext(ioDispatcher) {
      accountsDao.save(
        AccountEntity(
          name = name,
          balance = balance,
          currencyCode = currency.currencyCode,
          iconId = icon.id,
          colorId = color.id,
          ordinalIndex = accountsList.value.size,
        )
      )
    }
  }

  override suspend fun updateAccount(
    id: Long,
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: com.emendo.expensestracker.model.ui.ColorModel,
    balance: BigDecimal,
  ) {
    withContext(ioDispatcher) {
      accountsDao.updateAccountDetail(
        AccountDetailUpdate(
          id = id,
          name = name,
          balance = balance,
          currencyCode = currency.currencyCode,
          iconId = icon.id,
          colorId = color.id,
        )
      )
    }
  }

  override suspend fun updateOrdinalIndex(newOrderedList: Set<AccountWithOrdinalIndex>) {
    databaseUtils.expeWithTransaction {
      newOrderedList.forEach { update ->
        accountsDao.updateOrdinalIndex(
          AccountOrdinalIndexUpdate(
            id = update.id,
            ordinalIndex = update.ordinalIndex,
          )
        )
      }
    }
  }

  override suspend fun deleteAccount(id: Long) {
    withContext(ioDispatcher) {
      accountsDao.deleteById(id)
    }
  }
}