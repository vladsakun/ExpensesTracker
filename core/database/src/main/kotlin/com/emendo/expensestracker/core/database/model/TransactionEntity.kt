package com.emendo.expensestracker.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.util.ACCOUNT_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.CATEGORY_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.TABLE_TRANSACTION
import kotlinx.datetime.Instant
import java.math.BigDecimal

@Entity(
  tableName = TABLE_TRANSACTION,
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = [ACCOUNT_PRIMARY_KEY],
      childColumns = ["sourceAccountId"],
      onDelete = CASCADE,
    ),
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = [ACCOUNT_PRIMARY_KEY],
      childColumns = ["targetAccountId"],
      onDelete = CASCADE,
    ),
    ForeignKey(
      entity = CategoryEntity::class,
      parentColumns = [CATEGORY_PRIMARY_KEY],
      childColumns = ["targetCategoryId"],
      onDelete = CASCADE,
    ),
  ]
)
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val date: Instant,
  @ColumnInfo(index = true)
  val sourceAccountId: Long,
  @ColumnInfo(index = true)
  val targetAccountId: Long? = null,
  @ColumnInfo(index = true)
  val targetCategoryId: Long? = null,
  val value: BigDecimal,
  val currencyCode: String,
  val note: String? = null,
  val typeId: Int,

  val transferReceivedCurrencyCode: String? = null,
  val transferReceivedValue: BigDecimal? = null,
)