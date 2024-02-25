package com.emendo.expensestracker.categories.create

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import com.emendo.expensestracker.model.ui.textValueOf
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

@Stable
data class CreateCategoryScreenData(
  override val title: TextValue.Value,
  override val icon: IconModel,
  override val color: ColorModel,
  override val confirmButtonEnabled: Boolean = false,
  val navigateUpEvent: StateEvent = consumed,
) : CategoryScreenData {
  companion object {
    fun getDefault() = CreateCategoryScreenData(
      title = textValueOf(""),
      icon = IconModel.random,
      color = ColorModel.random,
    )
  }
}