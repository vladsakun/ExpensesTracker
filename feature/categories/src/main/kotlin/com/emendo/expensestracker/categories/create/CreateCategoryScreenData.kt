package com.emendo.expensestracker.categories.create

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel

@Stable
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