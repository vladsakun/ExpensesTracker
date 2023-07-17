package com.emendo.expensestracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val balance: Double,
  val currencyId: Long,
  val iconId: Long,
  val colorId: Long,
)