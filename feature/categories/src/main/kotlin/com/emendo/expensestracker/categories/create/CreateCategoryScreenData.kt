package com.emendo.expensestracker.categories.create

import com.emendo.expensestracker.categories.detail.CategoryScreenDataContract
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

data class CategoryCreateScreenDataImpl(
  override var title: TextValue,
  override var icon: IconModel,
  override var color: ColorModel,
  override var confirmButtonEnabled: Boolean,
  val navigateUpEvent: StateEvent = consumed,
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