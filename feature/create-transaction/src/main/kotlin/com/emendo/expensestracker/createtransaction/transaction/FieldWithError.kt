package com.emendo.expensestracker.createtransaction.transaction

sealed interface FieldWithError {
  data object Amount : FieldWithError
  data object Source : FieldWithError
}