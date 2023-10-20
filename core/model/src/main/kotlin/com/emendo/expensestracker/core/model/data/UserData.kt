package com.emendo.expensestracker.core.model.data

data class UserData(
  val useDynamicColor: Boolean,
  val generalCurrencyCode: String,
  val shouldShowNotifications: Boolean,
  val isBackupEnabled: Boolean,
)