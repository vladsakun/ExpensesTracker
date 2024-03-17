package com.emendo.expensestracker.accounts.create

import com.emendo.expensestracker.accounts.api.CreateAccountScreenApi
import com.emendo.expensestracker.accounts.destinations.CreateAccountRouteDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class CreateAccountScreenApiImpl @Inject constructor() : CreateAccountScreenApi {

  override fun getCreateAccountScreenRoute(): String = CreateAccountRouteDestination.route
}