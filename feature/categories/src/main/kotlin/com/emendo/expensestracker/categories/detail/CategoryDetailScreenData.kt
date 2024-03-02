package com.emendo.expensestracker.categories.detail

import com.emendo.expensestracker.categories.common.CategoryScreenData
import com.emendo.expensestracker.categories.common.CategoryScreenState

data class CategoryDetailScreenDataImpl(
  override val categoryScreenData: CategoryScreenData,
) : CategoryScreenState {

  override fun copyScreenData(categoryScreenData: CategoryScreenData): CategoryScreenState =
    copy(categoryScreenData = categoryScreenData)
}
