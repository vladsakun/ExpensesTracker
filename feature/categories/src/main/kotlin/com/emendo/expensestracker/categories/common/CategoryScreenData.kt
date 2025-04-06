package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.TextValue
import kotlinx.collections.immutable.ImmutableList

interface CategoryScreenState {
  val categoryScreenData: CategoryScreenData

  fun copyScreenData(categoryScreenData: CategoryScreenData = this.categoryScreenData): CategoryScreenState
}

data class CategoryScreenData(
  val confirmButtonEnabled: Boolean,
  val title: TextValue,
  val icon: IconModel,
  val color: ColorModel,
  val subcategories: ImmutableList<SubcategoryUiModel>,
)

data class SubcategoryUiModel(
  val id: Long?,
  val icon: IconModel,
  val name: String,
)