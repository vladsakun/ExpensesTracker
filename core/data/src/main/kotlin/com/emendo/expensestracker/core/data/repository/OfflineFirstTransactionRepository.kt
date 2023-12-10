package com.emendo.expensestracker.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.data.mapper.TransactionMapper
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.repository.api.TransactionRepository
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.util.DatabaseUtils
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.math.BigDecimal
import javax.inject.Inject

private const val PAGE_SIZE = 50

class OfflineFirstTransactionRepository @Inject constructor(
  private val accountDao: AccountDao,
  private val transactionDao: TransactionDao,
  private val databaseUtils: DatabaseUtils,
  private val transactionMapper: TransactionMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TransactionRepository {

  private val transactionsFullState = transactionDao.getTransactionFull()
    .map { transactionFullList ->
      transactionFullList
        .map { transactionMapper.map(it) }
        .sortedByDescending { it.date }
    }

  override val transactionsFull: Flow<List<TransactionModel>>
    get() = transactionsFullState

  override val lastTransactionFull: Flow<TransactionModel?>
    get() = transactionDao
      .getLastTransaction()
      .map { transactionFull ->
        transactionFull?.let { transactionMapper.map(it) }
      }

  override fun getTransactionsPager(): Flow<PagingData<TransactionModel>> =
    Pager(
      config = PagingConfig(pageSize = PAGE_SIZE),
      pagingSourceFactory = { transactionDao.transactionsPagingSource() }
    )
      .flow
      .map { pagingData -> pagingData.map { transactionMapper.map(it) } }

  override suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
    sourceCurrency: CurrencyModel?,
    targetCurrency: CurrencyModel?,
    transferAmount: BigDecimal?,
    note: String?,
  ) {
    withContext(ioDispatcher) {
      when {
        source is AccountModel && target is CategoryModel -> {
          when (target.type) {
            CategoryType.EXPENSE -> createExpenseTransaction(source, target, amount, note)
            CategoryType.INCOME -> createIncomeTransaction(source, target, amount, note)
          }
        }

        source is AccountModel && target is AccountModel -> createTransferTransaction(source, target, amount, note)
        else -> throw IllegalArgumentException("Unsupported transaction type")
      }
    }
  }

  override suspend fun retrieveLastTransactionFull(): TransactionModel? = withContext(ioDispatcher) {
    transactionDao.retrieveLastTransaction()?.let { transactionMapper.map(it) }
  }

  override suspend fun retrieveTransaction(id: Long): TransactionModel? = withContext(ioDispatcher) {
    transactionDao.retrieveTransactionById(id)?.let { transactionMapper.map(it) }
  }

  override suspend fun deleteTransaction(id: Long) {
    withContext(ioDispatcher) {
      transactionDao.deleteById(id)
    }
  }

  private suspend fun createExpenseTransaction(
    source: AccountModel,
    target: CategoryModel,
    amount: BigDecimal,
    note: String?,
    currencyModel: CurrencyModel = source.currency,
    date: Instant = Clock.System.now(),
  ) {
    withContext(ioDispatcher) {
      databaseUtils.expeWithTransaction {
        val newBalance = source.balance - amount
        accountDao.updateBalance(source.id, newBalance)
        transactionDao.save(
          TransactionEntity(
            sourceAccountId = source.id,
            targetCategoryId = target.id,
            value = amount,
            currencyCode = currencyModel.currencyCode,
            date = date,
            note = note,
          )
        )
      }
    }
  }

  private suspend fun createIncomeTransaction(
    source: AccountModel,
    target: CategoryModel,
    amount: BigDecimal,
    note: String?,
    currency: CurrencyModel = source.currency,
    date: Instant = Clock.System.now(),
  ) {
    withContext(ioDispatcher) {
      databaseUtils.expeWithTransaction {
        val newBalance = source.balance + amount
        accountDao.updateBalance(source.id, newBalance)
        transactionDao.save(
          TransactionEntity(
            sourceAccountId = source.id,
            targetCategoryId = target.id,
            value = amount,
            currencyCode = currency.currencyCode,
            date = date,
            note = note,
          )
        )
      }
    }
  }

  private suspend fun createTransferTransaction(
    source: AccountModel,
    target: AccountModel,
    amount: BigDecimal,
    note: String?,
    currency: CurrencyModel = source.currency,
    date: Instant = Clock.System.now(),
    transferReceivedCurrency: CurrencyModel = target.currency,
    transferReceivedAmount: BigDecimal = amount,
  ) {
    withContext(ioDispatcher) {
      databaseUtils.expeWithTransaction {
        val newSourceBalance = source.balance - amount
        val newTargetBalance = target.balance + amount

        accountDao.updateBalance(source.id, newSourceBalance)
        accountDao.updateBalance(target.id, newTargetBalance)
        transactionDao.save(
          TransactionEntity(
            sourceAccountId = source.id,
            targetAccountId = target.id,
            value = amount,
            currencyCode = currency.currencyCode,
            date = date,
            note = note,
            transferReceivedCurrencyCode = transferReceivedCurrency.currencyCode,
            transferReceivedValue = transferReceivedAmount,
          )
        )
      }
    }
  }
}