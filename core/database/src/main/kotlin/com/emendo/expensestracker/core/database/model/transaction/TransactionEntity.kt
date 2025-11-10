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

  /*
  Главная причина, по которой вам нужна таблица ExchangeRate, — это способность конвертировать сумму из валюты транзакции в текущую базовую валюту приложения (которая меняется).

  Пример сценария:
  Пользователь тратит $100 \text{CZK}$ (currencyOriginal).
  Вы сохраняете в транзакции: usdToOriginalRate ($24.50$).
  Пользователь устанавливает базовую валюту приложения как $\text{EUR}$.

  Проблема: Как конвертировать $\text{CZK}$ в $\text{EUR}$?
  Вы не можете взять курс $\text{CZK} \to \text{EUR}$ напрямую из транзакции.
  Вам нужна кросс-конвертация через $\text{USD}$:

  $$
  \text{Сумма в EUR} = \text{amountOriginal} \times \frac{\text{Курс}_{\text{USD} \to \text{EUR}} \text{(на дату)}}{\text{usdToOriginalRate} \text{(из транзакции)}}
  $$

  Значение из транзакции: amountOriginal ($100 \text{CZK}$) и usdToOriginalRate ($24.50$).
  Недостающее значение: $\text{Курс}_{\text{USD} \to \text{EUR}}$ на дату транзакции.
  Именно это недостающее значение, исторический курс $\text{USD} \to \text{EUR}$, вы должны получить из таблицы ExchangeRate.
  */
  val usdToOriginalRate: BigDecimal,

  val transferReceivedCurrencyCode: String? = null,
  val transferReceivedValue: BigDecimal? = null,
)