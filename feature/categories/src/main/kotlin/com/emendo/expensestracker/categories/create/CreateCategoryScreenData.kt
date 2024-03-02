package com.emendo.expensestracker.categories.create

import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.categories.common.CategoryScreenState
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.textValueOf
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

data class CategoryCreateScreenData(
  override val categoryScreenData: CategoryScreenData,
  val navigateUpEvent: StateEvent = consumed,
) : CategoryScreenState {

  override fun copyScreenData(categoryScreenData: CategoryScreenData): CategoryScreenState =
    copy(categoryScreenData = categoryScreenData)

  companion object {
    fun getDefault() = CategoryCreateScreenData(
      categoryScreenData = CategoryScreenData(
        title = textValueOf(""),
        icon = IconModel.random,
        color = ColorModel.random,
        confirmButtonEnabled = false,
      )
    )
  }
}