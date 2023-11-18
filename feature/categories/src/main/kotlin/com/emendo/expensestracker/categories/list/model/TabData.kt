package com.emendo.expensestracker.categories.list.model

import androidx.annotation.StringRes
import com.emendo.expensestracker.core.data.model.category.CategoryType

data class TabData(
  @StringRes val titleResId: Int,
  val categoryType: CategoryType,
)