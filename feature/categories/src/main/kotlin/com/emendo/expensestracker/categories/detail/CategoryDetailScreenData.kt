package com.emendo.expensestracker.categories.detail

import androidx.compose.runtime.Stable
import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue

@Stable
data class CategoryDetailScreenData(
  override val title: TextValue,
  override val icon: IconModel,
  override val color: ColorModel,
  override val confirmButtonEnabled: Boolean = false,
) : CategoryScreenData {
  companion object {
    fun getDefault(categoryModel: CategoryModel) = with(categoryModel) {
      CategoryDetailScreenData(
        title = name,
        icon = icon,
        color = color,
      )
    }
  }
}