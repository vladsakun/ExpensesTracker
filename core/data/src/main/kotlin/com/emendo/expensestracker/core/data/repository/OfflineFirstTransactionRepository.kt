package com.emendo.expensestracker.core.data.repository

import com.emendo.expensestracker.core.data.amount.AmountFormatter
import com.emendo.expensestracker.core.data.model.*
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.util.DatabaseUtils
import com.emendo.expensestracker.core.model.data.TransactionSource
import com.emendo.expensestracker.core.model.data.TransactionTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class OfflineFirstTransactionRepository @Inject constructor(
  private val accountDao: AccountDao,
  private val transactionDao: TransactionDao,
  private val databaseUtils: DatabaseUtils,
  private val amountFormatter: AmountFormatter,
) : TransactionRepository {

  override suspend fun createTransaction(
    source: TransactionSource,
    target: TransactionTarget,
    amount: BigDecimal,
  ) {
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

  override fun getTransactionsFull(): Flow<List<TransactionModel>> =
    transactionDao.getTransactionFull().map { transactionFullList ->
      transactionFullList.map { it.asExternalModel(amountFormatter) }
    }

  private suspend fun createExpenseTransaction(source: AccountModel, target: CategoryModel, amount: BigDecimal) {
    withContext(Dispatchers.IO) {
      databaseUtils.expeWithTransaction {
        val newBalance = source.balance - amount
        accountDao.updateBalance(source.id, newBalance)
        transactionDao.save(
          TransactionEntity(
            sourceAccountId = source.id,
            targetCategoryId = target.id,
            value = amount,
            currencyId = 1,
          )
        )
      }
    }
  }

  private suspend fun createIncomeTransaction(source: AccountModel, target: CategoryModel, amount: BigDecimal) {
    withContext(Dispatchers.IO) {
      databaseUtils.expeWithTransaction {
        val newBalance = source.balance + amount
        accountDao.updateBalance(source.id, newBalance)
        transactionDao.save(
          TransactionEntity(
            sourceAccountId = source.id,
            targetCategoryId = target.id,
            value = amount,
            currencyId = 1,
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
            sourceAccountId = source.id,
            targetAccountId = target.id,
            value = amount,
            currencyId = 1,
          )
        )
      }
    }
  }
}