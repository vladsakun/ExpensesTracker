package com.emendo.expensestracker.core.model.data

import kotlinx.serialization.Serializable

@Serializable
data class CategoryWithOrdinalIndex(
  val id: Long,
  val ordinalIndex: Int,
)