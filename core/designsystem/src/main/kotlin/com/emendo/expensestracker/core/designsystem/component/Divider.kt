package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
fun ExpeDivider(
  modifier: Modifier = Modifier,
  color: Color = DividerDefaults.color,
) {
  HorizontalDivider(
    modifier = modifier,
    thickness = Dimens.divider_thickness,
    color = color
  )
}