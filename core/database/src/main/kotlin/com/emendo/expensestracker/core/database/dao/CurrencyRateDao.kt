package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.CurrencyRateEntity
import com.emendo.expensestracker.core.database.util.TABLE_CURRENCY_RATE
import com.emendo.expensestracker.core.database.util.TARGET_CURRENCY_CODE_KEY
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
abstract class CurrencyRateDao : BaseDao<CurrencyRateEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<CurrencyRateEntity>>

  @Query("SELECT target_currency_code FROM $TABLE_NAME")
  abstract fun getCurrencyCodes(): Flow<List<String>>

  @Query("SELECT target_currency_code FROM $TABLE_NAME")
  abstract suspend fun retrieveAllCurrencyCodes(): List<String>

  @Query("SELECT * FROM $TABLE_NAME WHERE $TARGET_CURRENCY_CODE_KEY = :targetCode AND rate_date = :date")
  abstract suspend fun retrieveCurrencyRate(targetCode: String, date: String): CurrencyRateEntity?

  @Query("SELECT * FROM $TABLE_NAME WHERE rate_date = :date")
  abstract suspend fun retrieveCurrencyRates(date: String): List<CurrencyRateEntity>

  @Query("SELECT * FROM $TABLE_NAME")
  abstract suspend fun retrieveAllCurrencyRates(): List<CurrencyRateEntity>

  @Query("SELECT rate_multiplier FROM $TABLE_NAME WHERE $TARGET_CURRENCY_CODE_KEY = :targetCode")
  abstract suspend fun getRate(targetCode: String): BigDecimal?

  @Query("SELECT (SELECT COUNT(*) FROM $TABLE_NAME) == 0")
  abstract suspend fun isEmpty(): Boolean

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  companion object {
    private const val TABLE_NAME = TABLE_CURRENCY_RATE
  }
}