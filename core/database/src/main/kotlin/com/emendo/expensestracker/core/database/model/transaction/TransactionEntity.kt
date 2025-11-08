package com.emendo.expensestracker.core.database.model.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.model.account.AccountEntity
import com.emendo.expensestracker.core.database.model.category.CategoryEntity
import com.emendo.expensestracker.core.database.model.category.SubcategoryEntity
import com.emendo.expensestracker.core.database.util.ACCOUNT_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.CATEGORY_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.SUBCATEGORY_PRIMARY_KEY
import com.emendo.expensestracker.core.database.util.TABLE_TRANSACTION
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
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
    ForeignKey(
      entity = SubcategoryEntity::class,
      parentColumns = [SUBCATEGORY_PRIMARY_KEY],
      childColumns = ["targetSubcategoryId"],
      onDelete = CASCADE,
    )
  ]
)
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val date: Instant,
  val timeZoneId: String = TimeZone.currentSystemDefault().id,
  @ColumnInfo(index = true)
  val sourceAccountId: Long,
  @ColumnInfo(index = true)
  val targetAccountId: Long? = null,
  @ColumnInfo(index = true)
  val targetCategoryId: Long? = null,
  @ColumnInfo(index = true)
  val targetSubcategoryId: Long? = null,
  val value: BigDecimal,
  val currencyCode: String,
  val note: String? = null,
  val typeId: Int,

  val transferReceivedCurrencyCode: String? = null,
  val transferReceivedValue: BigDecimal? = null,
)