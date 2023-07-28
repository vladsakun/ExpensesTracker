package com.emendo.expensestracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.dao.CategoryDao
import com.emendo.expensestracker.core.database.model.AccountEntity
import com.emendo.expensestracker.core.database.model.CategoryEntity

@Database(
  entities = [
    AccountEntity::class,
    CategoryEntity::class,
  ],
  version = 1,
  exportSchema = true
)
abstract class ExpDatabase : RoomDatabase() {
  abstract fun accountDao(): AccountDao
  abstract fun categoryDao(): CategoryDao
}