package com.emendo.expensestracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emendo.expensestracker.core.database.dao.*
import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.core.database.model.account.AccountEntity
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.model.category.SubcategoryEntity
import com.emendo.expensestracker.core.database.model.transaction.TransactionEntity
import com.emendo.expensestracker.core.database.util.Converter

@Database(
  entities = [
    AccountEntity::class,
    CategoryEntity::class,
    SubcategoryEntity::class,
    TransactionEntity::class,
    CurrencyRateEntity::class,
  ],
  version = 1,
  exportSchema = true,
)
@TypeConverters(Converter::class)
abstract class ExpDatabase : RoomDatabase() {
  abstract fun accountDao(): AccountDao
  abstract fun categoryDao(): CategoryDao
  abstract fun transactionDao(): TransactionDao
  abstract fun currencyRateDao(): CurrencyRateDao
  abstract fun subcategoryDao(): SubcategoryDao
}