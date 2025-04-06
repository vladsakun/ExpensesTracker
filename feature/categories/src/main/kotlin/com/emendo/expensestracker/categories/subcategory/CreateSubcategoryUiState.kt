package com.emendo.expensestracker.categories.subcategory

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel

data class CreateSubcategoryUiState(
  val title: String,
  val icon: IconModel,
  val color: ColorModel,
)