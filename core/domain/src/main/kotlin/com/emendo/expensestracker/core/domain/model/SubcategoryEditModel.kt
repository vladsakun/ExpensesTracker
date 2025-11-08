package com.emendo.expensestracker.core.domain.model

import com.emendo.expensestracker.core.app.resources.models.IconModel

data class SubcategoryEditModel(
  val id: Long?,
  val name: String,
  val icon: IconModel,
  val ordinalIndex: Int,
)