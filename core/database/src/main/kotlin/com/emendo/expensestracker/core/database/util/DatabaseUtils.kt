package com.emendo.expensestracker.core.database.util

import androidx.room.withTransaction
import com.emendo.expensestracker.core.database.ExpDatabase
import javax.inject.Inject

class DatabaseUtils @Inject constructor(private val database: ExpDatabase) {
  suspend fun <R> expeWithTransaction(block: suspend () -> R): R = database.withTransaction(block)
}