package com.emendo.expensestracker.core.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.emendo.expensestracker.core.app.resources.models.TextValue
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient

@Composable
@ReadOnlyComposable
fun TextValue.stringValue(): String =
  when (val name = this) {
    is TextValue.Value -> name.value
    is TextValue.Resource -> stringResource(id = name.resId)
  }

@SuppressLint("ComposableNaming")
@Composable
fun <T> OpenResultRecipient<T>.handleValueResult(valueAction: (T) -> Unit) {
  onNavResult { result ->
    when (result) {
      is NavResult.Value -> valueAction(result.value)
      else -> Unit
    }
  }
}