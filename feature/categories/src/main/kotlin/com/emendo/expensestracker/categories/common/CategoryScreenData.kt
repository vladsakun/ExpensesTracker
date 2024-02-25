package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

interface CategoryScreenData {
  val title: TextValue
  val icon: IconModel
  val color: ColorModel
  val confirmButtonEnabled: Boolean
}