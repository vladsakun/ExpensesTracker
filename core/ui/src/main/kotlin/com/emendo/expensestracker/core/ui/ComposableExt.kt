package com.emendo.expensestracker.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.emendo.expensestracker.core.app.resources.models.TransactionElementName

@Composable
@ReadOnlyComposable
fun TransactionElementName.stringValue(): String =
  when (val name = this) {
    is TransactionElementName.Name -> name.value
    is TransactionElementName.NameStringRes -> stringResource(id = name.value)
  }

@Composable
@ReadOnlyComposable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
@ReadOnlyComposable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }