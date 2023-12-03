package com.emendo.expensestracker.core.app.resources.models

import androidx.annotation.StringRes

sealed interface TextValue {
  data class Value(val value: String) : TextValue
  data class Resource(@StringRes val resId: Int) : TextValue
}