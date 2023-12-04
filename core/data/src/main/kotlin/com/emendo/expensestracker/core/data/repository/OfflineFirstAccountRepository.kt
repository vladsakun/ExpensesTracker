package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.ext.stateInLazilyList
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.mapper.AccountMapper
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.repository.api.AccountRepository
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class OfflineFirstAccountRepository @Inject constructor(
  private val accountsDao: AccountDao,
  private val accountMapper: AccountMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @ApplicationScope private val scope: CoroutineScope,
) : AccountRepository {

  private val accountsList = accountsDao.getAll()
    .map { accountEntities -> accountEntities.map { accountMapper.map(it) } }
    .stateInLazilyList(scope)

  override val accounts: Flow<List<AccountModel>>
    get() = accountsList

  override val accountsSnapshot: List<AccountModel>
    get() = accountsList.value

  override fun getLastAccount(): Flow<AccountModel?> {
    return accountsDao.getLastAccount().map { entity ->
      entity?.let { accountMapper.map(it) }
    }
  }

  override suspend fun retrieveLastAccount(): AccountModel? {
    return withContext(ioDispatcher) {
      accountsDao.retrieveLastAccount()?.let { accountMapper.map(it) }
    }
  }

  override fun getById(id: Long): Flow<AccountModel> {
    return accountsDao.getById(id).map(accountMapper::map)
  }

  override suspend fun retrieveById(id: Long): AccountModel? = withContext(ioDispatcher) {
    accountsDao.retrieveById(id)?.let { accountMapper.map(it) }
  }

  override suspend fun createAccount(
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: ColorModel,
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
        )
      )
    }
  }

  override suspend fun updateAccount(
    id: Long,
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: ColorModel,
    balance: BigDecimal,
  ) {
    withContext(ioDispatcher) {
      accountsDao.save(
        AccountEntity(
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

  override suspend fun deleteAccount(id: Long) {
    withContext(ioDispatcher) {
      accountsDao.deleteById(id)
    }
  }
}