package com.emendo.expensestracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emendo.expensestracker.core.database.util.TABLE_CATEGORY

@Entity(tableName = TABLE_CATEGORY)
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val iconId: Int,
  val colorId: Int,
  val type: Int,
  val currencyId: Int,
)