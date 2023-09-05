package com.emendo.expensestracker.core.app.common.result

fun StringBuilder.appendIfNotNull(value: String?): StringBuilder {
  if (value != null) {
    append(value)
  }
  return this
}