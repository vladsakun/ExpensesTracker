package com.emendo.expensestracker.app.base.api

import com.emendo.expensestracker.data.api.model.category.CategoryType

sealed interface AppNavigationEvent {

  data class CreateCategory(val categoryType: CategoryType) : AppNavigationEvent
}