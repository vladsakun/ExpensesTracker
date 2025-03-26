package com.emendo.expensestracker.accounts.list

import com.emendo.expensestracker.accounts.api.SelectAccountArgs
import com.emendo.expensestracker.accounts.api.SelectAccountScreenApi
import com.emendo.expensestracker.accounts.destinations.AccountsScreenRouteDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SelectAccountScreenApiImpl @Inject constructor() : SelectAccountScreenApi {

  override fun getSelectAccountScreenRoute(args: SelectAccountArgs): String =
    AccountsScreenRouteDestination(args).route
}