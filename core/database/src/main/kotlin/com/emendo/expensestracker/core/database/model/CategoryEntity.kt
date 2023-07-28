package com.emendo.expensestracker.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity constructor(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val iconId: Int,
  val colorId: Int,
)