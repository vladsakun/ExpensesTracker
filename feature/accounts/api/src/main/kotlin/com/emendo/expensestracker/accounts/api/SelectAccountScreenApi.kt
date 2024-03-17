package com.emendo.expensestracker.accounts.api

interface SelectAccountScreenApi {
  fun getSelectAccountScreenRoute(isTransferTargetSelect: Boolean = false): String
}