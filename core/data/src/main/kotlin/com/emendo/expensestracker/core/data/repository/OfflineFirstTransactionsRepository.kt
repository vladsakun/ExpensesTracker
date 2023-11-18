package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.data.mapper.TransactionMapper
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.transaction.TransactionModel
import com.emendo.expensestracker.core.data.model.transaction.TransactionSource
import com.emendo.expensestracker.core.data.model.transaction.TransactionTarget
import com.emendo.expensestracker.core.data.repository.api.TransactionsRepository
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.util.DatabaseUtils
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.math.BigDecimal
import javax.inject.Inject

class OfflineFirstTransactionsRepository @Inject constructor(
  private val accountDao: AccountDao,
  private val transactionDao: TransactionDao,
  private val databaseUtils: DatabaseUtils,
  private val transactionMapper: TransactionMapper,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TransactionsRepository {

  override suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
    sourceCurrency: CurrencyModel?,
    targetCurrency: CurrencyModel?,
    transferAmount: BigDecimal?,
  ) {
    withContext(ioDispatcher) {
      when {
        source is AccountModel && target is CategoryModel -> {
          when (target.type) {
            CategoryType.EXPENSE -> createExpenseTransaction(source, target, amount)
            CategoryType.INCOME -> createIncomeTransaction(source, target, amount)
          }
        }

        source is AccountModel && target is AccountModel -> createTransferTransaction(source, target, amount)
        else -> throw IllegalArgumentException("Unsupported transaction type")
      }
    }
  }

  override fun getTransactionsFull(): Flow<List<TransactionModel>> =
    transactionDao.getTransactionFull().map { transactionFullList ->
      transactionFullList.map { transactionMapper.map(it) }
    }

  private suspend fun createExpenseTransaction(
    source: AccountModel,
    target: CategoryModel,
    amount: BigDecimal,
    currencyModel: CurrencyModel = source.currency,
    date: Instant = Clock.System.now(),
  ) {
    withContext(Dispatchers.IO) {
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
          )
        )
      }
    }
  }

  private suspend fun createIncomeTransaction(
    source: AccountModel,
    target: CategoryModel,
    amount: BigDecimal,
    currency: CurrencyModel = source.currency,
    date: Instant = Clock.System.now(),
  ) {
    withContext(Dispatchers.IO) {
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
          )
        )
      }
    }
  }

  private suspend fun createTransferTransaction(
    source: AccountModel,
    target: AccountModel,
    amount: BigDecimal,
    currency: CurrencyModel = source.currency,
    date: Instant = Clock.System.now(),
    transferReceivedCurrency: CurrencyModel = target.currency,
    transferReceivedAmount: BigDecimal = amount,
  ) {
    withContext(Dispatchers.IO) {
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
            transferReceivedCurrencyCode = transferReceivedCurrency.currencyCode,
            transferReceivedValue = transferReceivedAmount,
          )
        )
      }
    }
  }
}