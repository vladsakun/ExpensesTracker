package com.emendo.expensestracker.core.model.data.exception

abstract class ActionableException(
  errorMessage: String,
) : Throwable() {
  abstract val actionable: Actionable<*>
  override val message = errorMessage
}