package com.emendo.expensestracker.createtransaction.transaction.data

sealed interface FieldWithError {
  data object Amount : FieldWithError
  data object Source : FieldWithError
  data object TransferTarget : FieldWithError
}