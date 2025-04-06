package com.emendo.expensestracker.core.domain.model

import com.emendo.expensestracker.core.app.resources.models.IconModel

data class SubcategoryCreateModel(
  val name: String,
  val icon: IconModel,
  val ordinalIndex: Int,
)