package com.emendo.expensestracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.CategoryDao
import com.emendo.expensestracker.core.database.dao.CurrencyRateDao
import com.emendo.expensestracker.core.database.dao.TransactionDao
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.database.model.CategoryEntity
import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.util.Converter

@Database(
  entities = [
    AccountEntity::class,
    CategoryEntity::class,
    TransactionEntity::class,
    CurrencyRateEntity::class,
  ],
  version = 1,
  exportSchema = true
)
@TypeConverters(Converter::class)
abstract class ExpDatabase : RoomDatabase() {
  abstract fun accountDao(): AccountDao
  abstract fun categoryDao(): CategoryDao
  abstract fun transactionDao(): TransactionDao
  abstract fun currencyRateDao(): CurrencyRateDao
}