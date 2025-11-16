package com.emendo.expensestracker.createtransaction.selectcategory

import com.emendo.expensestracker.create.transaction.api.SelectCategoryScreenApi
import com.emendo.expensestracker.createtransaction.destinations.SelectCategoryScreenDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SelectCategoryScreenApiImpl @Inject constructor() : SelectCategoryScreenApi {

  override fun getRoute(): String = SelectCategoryScreenDestination.route
}