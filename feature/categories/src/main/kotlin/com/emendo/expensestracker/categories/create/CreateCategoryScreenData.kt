package com.emendo.expensestracker.categories.create

import com.emendo.expensestracker.categories.common.CategoryScreenData
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed

data class CreateCategoryAdditionalScreenData(
  val navigateUpEvent: StateEvent = consumed,
)
typealias CreateCategoryScreenData = CategoryScreenData<CreateCategoryAdditionalScreenData>