package com.emendo.expensestracker.core.model.data.exception

fun interface Actionable<T> {
  fun action(): suspend () -> T
}