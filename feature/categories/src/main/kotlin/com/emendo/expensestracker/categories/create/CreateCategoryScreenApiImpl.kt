package com.emendo.expensestracker.categories.create

import com.emendo.expensestracker.categories.api.CreateCategoryScreenApi
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.data.api.model.category.CategoryType
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class CreateCategoryScreenApiImpl @Inject constructor() : CreateCategoryScreenApi {

  override fun getRoute(categoryType: CategoryType): String =
    CreateCategoryRouteDestination(categoryType).route
}