package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.data.model.Category
import com.emendo.expensestracker.core.data.model.CategoryType
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.database.model.TransactionType
import com.emendo.expensestracker.core.database.util.DatabaseUtils
import com.emendo.expensestracker.core.model.data.TransactionSource
import com.emendo.expensestracker.core.model.data.TransactionTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class OfflineFirstTransactionRepository @Inject constructor(
  private val accountDao: AccountDao,
  private val transactionDao: TransactionDao,
  private val databaseUtils: DatabaseUtils,
) : TransactionRepository {

  override suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
  ) {
    when {
      source is AccountModel && target is Category -> {
        when (target.type) {
          CategoryType.EXPENSE -> createExpenseTransaction(source, target, amount)
          CategoryType.INCOME -> createIncomeTransaction(source, target, amount)
        }
      }

      source is AccountModel && target is AccountModel -> createTransferTransaction(source, target, amount)
      else -> throw IllegalArgumentException("Unsupported transaction type")
    }
  }

  override fun getTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAll()
  override fun getAllTransactions(): Flow<List<TransactionFull>> = transactionDao.getTransactionFull()

  private suspend fun createExpenseTransaction(source: AccountModel, target: Category, amount: BigDecimal) {
    withContext(Dispatchers.IO) {
      databaseUtils.expeWithTransaction {
        val newBalance = source.balance - amount
        accountDao.updateBalance(source.id, newBalance)
        transactionDao.save(
          TransactionEntity(
            sourceId = source.id,
            targetId = target.id,
            value = amount,
            currencyId = 1,
            type = TransactionType.EXPENSE.ordinal
          )
        )
      }
    }
  }

  private suspend fun createIncomeTransaction(source: AccountModel, target: Category, amount: BigDecimal) {
    withContext(Dispatchers.IO) {
      databaseUtils.expeWithTransaction {
        val newBalance = source.balance + amount
        accountDao.updateBalance(source.id, newBalance)
        transactionDao.save(
          TransactionEntity(
            sourceId = source.id,
            targetId = target.id,
            value = amount,
            currencyId = 1,
            type = TransactionType.INCOME.ordinal
          )
        )
      }
    }
  }

  private suspend fun createTransferTransaction(source: AccountModel, target: AccountModel, amount: BigDecimal) {
    withContext(Dispatchers.IO) {
      databaseUtils.expeWithTransaction {
        val newSourceBalance = source.balance - amount
        val newTargetBalance = target.balance + amount

        accountDao.updateBalance(source.id, newSourceBalance)
        accountDao.updateBalance(target.id, newTargetBalance)
        transactionDao.save(
          TransactionEntity(
            sourceId = source.id,
            targetId = target.id,
            value = amount,
            currencyId = 1,
            type = TransactionType.TRANSFER.ordinal
          )
        )
      }
    }
  }
}