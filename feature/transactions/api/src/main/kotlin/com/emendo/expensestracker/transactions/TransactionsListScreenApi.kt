package com.emendo.expensestracker.transactions

interface TransactionsListScreenApi {
  fun getRoute(args: TransactionsListArgs?): String
}