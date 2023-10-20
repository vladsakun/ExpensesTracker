package com.emendo.expensestracker.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorsPalette(
  val successColor: Color = Color.Unspecified,
)

val LightSuccessColor = Color(0xff35ab4f)

val DarkSuccessColor = Color(0xff35ab4f)

val LightCustomColorsPalette = CustomColorsPalette(
  successColor = LightSuccessColor
)

val DarkCustomColorsPalette = CustomColorsPalette(
  successColor = DarkSuccessColor
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

val MaterialTheme.customColorsPalette: CustomColorsPalette
  @Composable
  @ReadOnlyComposable
  get() = LocalCustomColorsPalette.current