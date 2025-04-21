package com.emendo.expensestracker.core.data.repository

import androidx.paging.*
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.data.mapper.TransactionMapper
import com.emendo.expensestracker.core.data.mapper.transactionType
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import com.emendo.expensestracker.core.database.model.transaction.TransactionEntity
import com.emendo.expensestracker.core.database.util.DatabaseUtils
import com.emendo.expensestracker.core.model.data.Amount
import com.emendo.expensestracker.core.model.data.TransactionType
import com.emendo.expensestracker.core.model.data.TransactionType.Companion.id
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.GetTransactionTypeUseCase
import com.emendo.expensestracker.data.api.extensions.asAccount
import com.emendo.expensestracker.data.api.extensions.asCategory
import com.emendo.expensestracker.data.api.model.AccountModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
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
  private val getTransactionTypeUseCase: GetTransactionTypeUseCase,
  @ApplicationScope private val scope: CoroutineScope,
) : TransactionRepository {

  override fun getLastTransactionFull(): Flow<TransactionModel?> =
    transactionDao
      .getLastTransaction()
      .map { transaction ->
        transaction?.let { transactionMapper.map(it) }
      }

  override fun getTransactionsPagingFlow(cacheScope: CoroutineScope): Flow<PagingData<TransactionModel>> =
    PagerDefault { transactionDao.transactionsPagingSource() }
      .map { pagingData -> pagingData.map { transactionMapper.map(it) } }
      .cachedIn(cacheScope)

  override suspend fun retrieveLastTransferTransaction(sourceAccountId: Long): TransactionModel? =
    transactionDao.retrieveLastTransferTransaction(sourceAccountId)?.let { transactionMapper.map(it) }

  override suspend fun retrieveFirstTransaction(): TransactionModel? =
    transactionDao.retrieveFirstTransaction()?.let { transactionMapper.map(it) }

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

  override fun getTransactionsInPeriod(from: Instant, to: Instant): Flow<List<TransactionModel>> =
    transactionDao
      .getTransactionsInPeriod(from, to)
      .map { transactions -> transactions.map { transactionMapper.map(it) } }

  override fun getTransactionsPagedInPeriod(
    targetCategoryId: Long,
    from: Instant,
    to: Instant,
  ): Flow<PagingData<TransactionModel>> =
    PagerDefault { transactionDao.transactionsInPeriodPagingSource(targetCategoryId, from, to) }
      .map { pagingData -> pagingData.map { transactionMapper.map(it) } }

  override fun getTransactionsPagedInPeriod(
    transactionType: TransactionType,
    from: Instant,
    to: Instant,
  ): Flow<PagingData<TransactionModel>> =
    PagerDefault { transactionDao.transactionsByTypeInPeriodPagingSource(transactionType.id, from, to) }
      .map { pagingData -> pagingData.map { transactionMapper.map(it) } }

  override suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    subcategoryId: Long?,
    amount: Amount,
    transferReceivedAmount: Amount?,
    note: String?,
  ) {
    when (getTransactionTypeUseCase(source, target)) {
      TransactionType.INCOME -> createIncomeTransaction(
        source = source.asAccount(),
        target = target.asCategory(),
        subcategoryId = subcategoryId,
        amount = amount,
        note = note
      )

      TransactionType.EXPENSE -> createExpenseTransaction(
        source = source.asAccount(),
        target = target.asCategory(),
        subcategoryId = subcategoryId,
        amount = amount,
        note = note
      )

      TransactionType.TRANSFER -> createTransferTransaction(
        source = source.asAccount(),
        target = target.asAccount(),
        amount = amount,
        note = note,
        transferReceivedAmount = checkNotNull(transferReceivedAmount) { "TransferReceivedAmount shouldn't be null in Transfer transaction" },
      )
    }
  }

  private suspend fun createExpenseTransaction(
    source: AccountModel,
    target: CategoryModel,
    subcategoryId: Long?,
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
          targetSubcategoryId = subcategoryId,
          value = amount.value.abs().negate(),
          currencyCode = amount.currency.currencyCode,
          date = date,
          note = note,
          typeId = TransactionType.EXPENSE.id,
        )
      )
    }
  }

  private suspend fun createIncomeTransaction(
    source: AccountModel,
    target: CategoryModel,
    subcategoryId: Long?,
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
          targetSubcategoryId = subcategoryId,
          value = amount.value.abs(),
          currencyCode = amount.currency.currencyCode,
          date = date,
          note = note,
          typeId = TransactionType.INCOME.id,
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
          typeId = TransactionType.TRANSFER.id,
        )
      )
    }
  }

  private fun <Key : Any, Value : Any> PagerDefault(pagingSourceFactory: () -> PagingSource<Key, Value>): Flow<PagingData<Value>> =
    Pager(config = PagingConfig(pageSize = PAGE_SIZE), pagingSourceFactory = pagingSourceFactory).flow
}