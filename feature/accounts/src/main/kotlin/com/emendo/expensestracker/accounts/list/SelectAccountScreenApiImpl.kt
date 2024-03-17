package com.emendo.expensestracker.accounts.list

import com.emendo.expensestracker.accounts.api.SelectAccountScreenApi
import com.emendo.expensestracker.accounts.destinations.AccountsScreenRouteDestination
import com.emendo.expensestracker.core.domain.api.CreateTransactionController
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class SelectAccountScreenApiImpl @Inject constructor(
  private val createTransactionController: CreateTransactionController,
) : SelectAccountScreenApi {

  override fun getSelectAccountScreenRoute(isTransferTargetSelect: Boolean): String {
    if (isTransferTargetSelect) {
      createTransactionController.startSelectTransferTargetFlow()
    } else {
      createTransactionController.startSelectSourceFlow()
    }

    return AccountsScreenRouteDestination.route
  }
}