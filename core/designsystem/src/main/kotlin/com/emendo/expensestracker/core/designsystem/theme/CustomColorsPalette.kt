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
  val neutralColor: Color = Color.Unspecified,
)

val LightSuccessColor = Color(0xff35ab4f)
val LightNeutralColor = Color(0xFF7A8B7E)

val DarkSuccessColor = Color(0xff35ab4f)
val DarkNeutralColor = Color(0xFFB7C9BB)

val LightCustomColorsPalette = CustomColorsPalette(
  successColor = LightSuccessColor,
  neutralColor = LightNeutralColor,
)

val DarkCustomColorsPalette = CustomColorsPalette(
  successColor = DarkSuccessColor,
  neutralColor = DarkNeutralColor,
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

val MaterialTheme.customColorsPalette: CustomColorsPalette
  @Composable
  @ReadOnlyComposable
  get() = LocalCustomColorsPalette.current