package com.emendo.expensestracker.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.emendo.expensestracker.core.database.common.BaseDao
import com.emendo.expensestracker.core.database.model.account.AccountDetailUpdate
import com.emendo.expensestracker.core.database.model.account.AccountEntity
import com.emendo.expensestracker.core.database.model.account.AccountOrdinalIndexUpdate
import com.emendo.expensestracker.core.database.util.ACCOUNT_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.TABLE_ACCOUNT
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
abstract class AccountDao : BaseDao<AccountEntity>() {

  @Query("SELECT * FROM $TABLE_NAME")
  abstract override fun getAll(): Flow<List<AccountEntity>>

  @Query("SELECT * FROM $TABLE_NAME")
  abstract suspend fun retrieveAll(): List<AccountEntity>

  @Query("SELECT * FROM $TABLE_NAME ORDER BY $PRIMARY_KEY LIMIT 1")
  abstract fun getLastAccount(): Flow<AccountEntity?>

  @Query("SELECT * FROM $TABLE_NAME ORDER BY $PRIMARY_KEY LIMIT 1")
  abstract suspend fun retrieveLastAccount(): AccountEntity?

  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract fun getById(id: Long): Flow<AccountEntity>

  @Query("SELECT * FROM $TABLE_NAME WHERE $PRIMARY_KEY = :id")
  abstract suspend fun retrieveById(id: Long): AccountEntity?

  @Query("DELETE FROM $TABLE_NAME")
  abstract override suspend fun deleteAll()

  @Query("UPDATE $TABLE_NAME SET balance = :balance WHERE $PRIMARY_KEY = :id")
  abstract fun updateBalance(id: Long, balance: BigDecimal)

  @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
  abstract fun deleteById(id: Long)

  @Update(entity = AccountEntity::class)
  abstract suspend fun updateAccountDetail(accountDetailUpdate: AccountDetailUpdate)

  @Update(entity = AccountEntity::class)
  abstract suspend fun updateOrdinalIndex(accountOrdinalIndexUpdate: AccountOrdinalIndexUpdate)

  companion object {
    private const val TABLE_NAME = TABLE_ACCOUNT
    private const val PRIMARY_KEY = ACCOUNT_PRIMARY_KEY
  }
}