package com.emendo.expensestracker.categories.api

import com.emendo.expensestracker.data.api.model.category.CategoryType

interface CreateCategoryScreenApi {

  fun getRoute(categoryType: CategoryType): String
}