package com.emendo.categories.create

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel

data class CreateCategoryScreenData(
  val title: String,
  val icon: IconModel,
  val color: ColorModel,
) {
  companion object {
    fun getDefault() = CreateCategoryScreenData(
      title = "",
      icon = IconModel.GOVERNMENT,
      color = ColorModel.GREEN,
    )
  }
}