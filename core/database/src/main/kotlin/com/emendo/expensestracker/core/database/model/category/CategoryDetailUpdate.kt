package com.emendo.expensestracker.core.database.model.category

import androidx.room.Entity

@Entity
data class CategoryDetailUpdate(
  val id: Long,
  val name: String,
  val iconId: Int,
  val colorId: Int,
  val type: Int,
)

@Entity
data class CategoryOrdinalIndexUpdate(
  val id: Long,
  val ordinalIndex: Int,
)