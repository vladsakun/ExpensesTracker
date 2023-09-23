package com.emendo.expensestracker.core.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.TransactionEntity
import com.emendo.expensestracker.core.database.model.TransactionFull
import com.emendo.expensestracker.core.database.util.TABLE_ACCOUNT
import com.emendo.expensestracker.core.database.util.TABLE_CATEGORY
import com.emendo.expensestracker.core.database.util.TABLE_TRANSACTION
import com.emendo.expensestracker.core.database.util.TRANSACTION_PRIMARY_KEY
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransactionDao : BaseDao<TransactionEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract fun getById(id: Long): Flow<TransactionEntity>

  @Query("SELECT * FROM $TABLE_NAME")
  abstract fun getAllCursor(): Cursor

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  @Query(
    //    "SELECT `transaction`.id, " +
    //      "`transaction`.currencyId, " +
    //      "`transaction`.sourceId, " +
    //      "`transaction`.targetId, " +
    //      "`transaction`.type, " +
    //      "`transaction`.value, " +
    "SELECT *, " +
      "account.id AS source_account_id, " +
      "account.name AS source_account_name, " +
      "account.balance AS source_account_balance, " +
      "account.currencyId AS source_account_currencyId, " +
      "account.iconId AS source_account_iconId, " +
      "account.colorId AS source_account_colorId, " +
      "target_account.id AS target_account_id, " +
      "target_account.name AS target_account_name, " +
      "target_account.balance AS target_account_balance, " +
      "target_account.currencyId AS target_account_currencyId, " +
      "target_account.iconId AS target_account_iconId, " +
      "target_account.colorId AS target_account_colorId, " +
      "target_category.id AS target_category_id, " +
      "target_category.name AS target_category_name, " +
      "target_category.iconId AS target_category_iconId, " +
      "target_category.colorId AS target_category_colorId, " +
      "target_category.type AS target_category_type, " +
      "target_category.currencyId AS target_category_currencyId " +
      "FROM $TABLE_NAME " +
      "LEFT JOIN $TABLE_ACCOUNT ON `transaction`.sourceId = account.id " +
      "LEFT JOIN $TABLE_ACCOUNT AS target_account ON `transaction`.targetId = target_account.id AND `transaction`.type = 2 " +
      "LEFT JOIN $TABLE_CATEGORY AS target_category ON `transaction`.targetId = target_category.id AND `transaction`.type = 1 "
  )
  abstract fun getTransactionFull(): Flow<List<TransactionFull>>

  @Upsert
  abstract fun upsert(transaction: TransactionEntity)

  companion object {
    private const val TABLE_NAME = "`$TABLE_TRANSACTION`"
    private const val PRIMARY_KEY = TRANSACTION_PRIMARY_KEY
  }

}