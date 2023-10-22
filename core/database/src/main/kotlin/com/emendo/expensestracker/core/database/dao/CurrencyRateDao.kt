package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.core.database.util.CURRENCY_RATE_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.TABLE_CURRENCY_RATE
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
abstract class CurrencyRateDao : BaseDao<CurrencyRateEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<CurrencyRateEntity>>

  @Query("SELECT * FROM $TABLE_NAME")
  abstract suspend fun retrieveAll(): List<CurrencyRateEntity>

  @Query("SELECT currencyCode FROM $TABLE_NAME")
  abstract suspend fun retrieveAllCurrencyCodes(): List<String>

  @Query("SELECT rate FROM $TABLE_NAME WHERE $PRIMARY_KEY = :currencyCode")
  abstract suspend fun getRate(currencyCode: String): BigDecimal

  @Query("SELECT (SELECT COUNT(*) FROM $TABLE_NAME) == 0")
  abstract suspend fun isEmpty(): Boolean

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  companion object {
    private const val TABLE_NAME = TABLE_CURRENCY_RATE
    private const val PRIMARY_KEY = CURRENCY_RATE_PRIMARY_KEY
  }
}