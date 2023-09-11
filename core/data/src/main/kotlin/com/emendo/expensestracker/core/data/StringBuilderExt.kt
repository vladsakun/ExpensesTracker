package com.emendo.expensestracker.core.data

fun StringBuilder.appendIfNotNull(value: String?): StringBuilder {
  if (value != null) {
    append(value)
  }
  return this
}