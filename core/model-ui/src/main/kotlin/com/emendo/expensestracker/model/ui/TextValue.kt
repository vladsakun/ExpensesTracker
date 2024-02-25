package com.emendo.expensestracker.model.ui

import androidx.annotation.StringRes

sealed interface TextValue {
  data class Value(val value: String) : TextValue
  data class Resource(@StringRes val resId: Int) : TextValue
}

fun textValueOf(value: String) = TextValue.Value(value)
fun resourceValueOf(@StringRes resId: Int) = TextValue.Resource(resId)

fun TextValue.textValueOrBlank() = if (this is TextValue.Value) value else ""
