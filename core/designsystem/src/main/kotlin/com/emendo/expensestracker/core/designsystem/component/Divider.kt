package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
fun ExpeDivider() {
  Divider(thickness = Dimens.divider_thickness)
}