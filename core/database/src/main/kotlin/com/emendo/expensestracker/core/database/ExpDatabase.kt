package com.emendo.expensestracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emendo.expensestracker.core.database.dao.AccountDao
import com.emendo.expensestracker.core.database.model.AccountEntity

@Database(
  entities = [
    AccountEntity::class,
  ],
  version = 1,
  exportSchema = true
)
abstract class ExpDatabase : RoomDatabase() {
  abstract fun accountDao(): AccountDao
}