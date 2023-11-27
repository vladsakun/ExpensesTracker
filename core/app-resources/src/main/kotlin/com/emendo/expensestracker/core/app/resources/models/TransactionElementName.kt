package com.emendo.expensestracker.core.app.resources.models

import androidx.annotation.StringRes

sealed interface TransactionElementName {
  data class Name(val value: String) : TransactionElementName
  data class NameStringRes(@StringRes val value: Int) : TransactionElementName
}