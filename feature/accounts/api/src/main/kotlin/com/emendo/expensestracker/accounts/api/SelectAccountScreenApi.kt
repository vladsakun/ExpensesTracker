package com.emendo.expensestracker.accounts.api

interface SelectAccountScreenApi {
  fun getSelectAccountScreenRoute(args: SelectAccountArgs): String
}