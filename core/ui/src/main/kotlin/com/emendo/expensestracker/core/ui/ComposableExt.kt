package com.emendo.expensestracker.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.emendo.expensestracker.core.app.resources.models.TextValue

@Composable
@ReadOnlyComposable
fun TextValue.stringValue(): String =
  when (val name = this) {
    is TextValue.Value -> name.value
    is TextValue.Resource -> stringResource(id = name.resId)
  }

@Composable
@ReadOnlyComposable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
@ReadOnlyComposable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }