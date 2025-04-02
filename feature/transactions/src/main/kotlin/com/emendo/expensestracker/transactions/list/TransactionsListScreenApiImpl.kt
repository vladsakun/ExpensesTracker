package com.emendo.expensestracker.transactions.list

import com.emendo.expensestracker.transactions.TransactionsListArgs
import com.emendo.expensestracker.transactions.TransactionsListScreenApi
import com.emendo.expensestracker.transactions.destinations.TransactionsListRouteDestination
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class TransactionsListScreenApiImpl @Inject constructor() : TransactionsListScreenApi {
  override fun getRoute(args: TransactionsListArgs?) = TransactionsListRouteDestination(args).route
}