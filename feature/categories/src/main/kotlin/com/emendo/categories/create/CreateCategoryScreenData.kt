package com.emendo.categories.create

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel

data class CreateCategoryScreenData(
  val title: String,
  val icon: IconModel,
  val color: ColorModel,
  val isCreateButtonEnabled: Boolean = false,
) {
  companion object {
    fun getDefault() = CreateCategoryScreenData(
      title = "",
      icon = IconModel.random,
      color = ColorModel.random,
    )
  }
}