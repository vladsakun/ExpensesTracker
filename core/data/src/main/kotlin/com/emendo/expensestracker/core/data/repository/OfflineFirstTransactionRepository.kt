package com.emendo.expensestracker.core.data.repository

import androidx.paging.*
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.mapper.TransactionMapper
import com.emendo.expensestracker.core.data.mapper.transactionType
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.util.DatabaseUtils
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.data.api.model.transaction.TransactionModel
import com.emendo.expensestracker.data.api.model.transaction.TransactionSource
import com.emendo.expensestracker.data.api.model.transaction.TransactionTarget
import com.emendo.expensestracker.data.api.model.transaction.TransactionValueWithType
import com.emendo.expensestracker.data.api.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

private const val PAGE_SIZE = 50

class OfflineFirstTransactionRepository @Inject constructor(
  private val accountDao: AccountDao,
  private val transactionDao: TransactionDao,
  private val databaseUtils: DatabaseUtils,
  private val transactionMapper: TransactionMapper,
  @ApplicationScope private val scope: CoroutineScope,
) : TransactionRepository {

  override fun getLastTransactionFull(): Flow<TransactionModel?> =
    transactionDao
      .getLastTransaction()
      .map { transaction ->
        transaction?.let { transactionMapper.map(it) }
      }

  override val transactionsPagingFlow: Flow<PagingData<TransactionModel>> by lazy {
    Pager(
      config = PagingConfig(pageSize = PAGE_SIZE),
      pagingSourceFactory = { transactionDao.transactionsPagingSource() }
    ).flow
      .map { pagingData -> pagingData.map { transactionMapper.map(it) } }
      .cachedIn(scope)
  }

  override suspend fun retrieveLastTransferTransaction(sourceAccountId: Long): TransactionModel? =
    transactionDao.retrieveLastTransferTransaction(sourceAccountId)?.let { transactionMapper.map(it) }

  override suspend fun deleteTransaction(id: Long) {
    transactionDao.deleteById(id)
  }

  override suspend fun retrieveTransactionsInPeriod(from: Instant, to: Instant): List<TransactionValueWithType> =
    transactionDao
      .retrieveTransactionsInPeriod(from, to).map { transaction ->
        TransactionValueWithType(
          type = transaction.transactionType,
          value = transaction.transactionEntity.value,
          currency = CurrencyModel.toCurrencyModel(transaction.transactionEntity.currencyCode),
        )
      }

  override suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: Amount,
    transferReceivedAmount: Amount?,
    note: String?,
  ) {
    when {
      source is AccountModel && target is CategoryModel -> {
        when (target.type) {
          CategoryType.EXPENSE -> createExpenseTransaction(source, target, amount, note)
          CategoryType.INCOME -> createIncomeTransaction(source, target, amount, note)
        }
      }

      source is AccountModel && target is AccountModel -> createTransferTransaction(
        source = source,
        target = target,
        amount = amount,
        note = note,
        transferReceivedAmount = checkNotNull(transferReceivedAmount) { "TransferReceivedAmount should not be null in Transfer transaction" },
      )

      else -> throw IllegalArgumentException("Unsupported transaction type with source: $source, target: $target")
    }
  }

  private suspend fun createExpenseTransaction(
    source: AccountModel,
    target: CategoryModel,
    amount: Amount,
    note: String?,
    date: Instant = Clock.System.now(),
  ) {
    databaseUtils.expeWithTransaction {
      val newBalance = source.balance.value - amount.value
      accountDao.updateBalance(source.id, newBalance)
      transactionDao.save(
        TransactionEntity(
          sourceAccountId = source.id,
          targetCategoryId = target.id,
          value = amount.value.abs().negate(),
          currencyCode = amount.currency.currencyCode,
          date = date,
          note = note,
        )
      )
    }
  }

  private suspend fun createIncomeTransaction(
    source: AccountModel,
    target: CategoryModel,
    amount: Amount,
    note: String?,
    date: Instant = Clock.System.now(),
  ) {
    databaseUtils.expeWithTransaction {
      val newBalance = source.balance.value + amount.value
      accountDao.updateBalance(source.id, newBalance)
      transactionDao.save(
        TransactionEntity(
          sourceAccountId = source.id,
          targetCategoryId = target.id,
          value = amount.value.abs(),
          currencyCode = amount.currency.currencyCode,
          date = date,
          note = note,
        )
      )
    }
  }

  private suspend fun createTransferTransaction(
    source: AccountModel,
    target: AccountModel,
    amount: Amount,
    note: String?,
    transferReceivedAmount: Amount,
    date: Instant = Clock.System.now(),
  ) {
    databaseUtils.expeWithTransaction {
      val newSourceBalance = source.balance.value - amount.value
      val newTargetBalance = target.balance.value + transferReceivedAmount.value

      accountDao.updateBalance(source.id, newSourceBalance)
      accountDao.updateBalance(target.id, newTargetBalance)
      transactionDao.save(
        TransactionEntity(
          sourceAccountId = source.id,
          targetAccountId = target.id,
          value = amount.value.abs().negate(),
          currencyCode = amount.currency.currencyCode,
          date = date,
          note = note,
          transferReceivedCurrencyCode = transferReceivedAmount.currency.currencyCode,
          transferReceivedValue = transferReceivedAmount.value.abs(),
        )
      )
    }
  }
}