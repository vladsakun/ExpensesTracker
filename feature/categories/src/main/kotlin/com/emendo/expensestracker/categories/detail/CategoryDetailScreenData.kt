package com.emendo.expensestracker.categories.detail

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

interface CategoryScreenDataContract {
  val title: TextValue
  val icon: IconModel
  val color: ColorModel
  val confirmButtonEnabled: Boolean

  fun copyMy(
    title: TextValue = this.title,
    icon: IconModel = this.icon,
    color: ColorModel = this.color,
    confirmButtonEnabled: Boolean = this.confirmButtonEnabled,
  ): CategoryScreenDataContract
}

data class CategoryDetailScreenDataImpl(
  override var title: TextValue,
  override var icon: IconModel,
  override var color: ColorModel,
  override var confirmButtonEnabled: Boolean,
  val test: Boolean = false,
) : CategoryScreenDataContract {

  override fun copyMy(
    title: TextValue,
    icon: IconModel,
    color: ColorModel,
    confirmButtonEnabled: Boolean,
  ): CategoryScreenDataContract {
    return copy(
      title = title,
      icon = icon,
      color = color,
      confirmButtonEnabled = confirmButtonEnabled,
    )
  }
}
