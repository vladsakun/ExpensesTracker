package com.emendo.expensestracker.core.database.model.budget

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.util.TABLE_BUDGET
import com.emendo.expensestracker.core.model.data.BudgetPeriod
import java.math.BigDecimal

@Entity(tableName = TABLE_BUDGET)
data class BudgetEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val limit: BigDecimal,
  val iconId: Int,
  val colorId: Int,
  val period: BudgetPeriod = BudgetPeriod.MONTHLY,
  val categoryId: Long,
  val currencyCode: String,
)
