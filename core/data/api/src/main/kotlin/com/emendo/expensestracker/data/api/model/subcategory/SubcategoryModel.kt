package com.emendo.expensestracker.data.api.model.subcategory

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel

data class SubcategoryModel(
  val id: Long,
  val name: String,
  val color: ColorModel,
  val icon: IconModel,
  val ordinalIndex: Int,
)