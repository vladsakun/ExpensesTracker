package com.emendo.expensestracker.core.database.model.category

import androidx.room.Entity

@Entity
data class SubcategoryDetailUpdate(
  val id: Long,
  val name: String,
  val iconId: Int,
)

@Entity
data class SubcategoryOrdinalIndexUpdate(
  val id: Long,
  val ordinalIndex: Int,
)