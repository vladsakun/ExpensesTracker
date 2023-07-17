package com.emendo.expensestracker.core.database.common

import androidx.room.Delete
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

abstract class BaseDao<T> {

  abstract fun getAll(): Flow<@JvmSuppressWildcards List<@JvmSuppressWildcards T>>
  abstract suspend fun deleteAll()

  @Upsert
  abstract suspend fun save(model: T)

  @Upsert
  abstract suspend fun save(models: List<T>)

  @Delete
  abstract suspend fun delete(model: T)
}