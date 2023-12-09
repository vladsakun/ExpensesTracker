package com.emendo.expensestracker.core.designsystem.utils

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
  if (condition) this.modifier() else this

fun LazyListScope.uniqueItem(key: String, content: @Composable LazyItemScope.() -> Unit) =
  item(key = key, contentType = key, content = content)

fun LazyGridScope.uniqueItem(key: String, content: @Composable LazyGridItemScope.() -> Unit) =
  item(key = key, contentType = key, content = content)

inline val screenHeightDp: Dp
  @Composable get() {
    return LocalConfiguration.current.screenHeightDp.dp
  }

inline val screenWidthDp: Dp
  @Composable get() {
    return LocalConfiguration.current.screenWidthDp.dp
  }

@Composable
@ReadOnlyComposable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
@ReadOnlyComposable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
@ReadOnlyComposable
fun TextUnit.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }