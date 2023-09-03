package com.emendo.expensestracker.core.designsystem.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
internal fun CalculatorRow(content: @Composable RowScope.() -> Unit) {
  Row(
    modifier = Modifier.height(Dimens.icon_button_size),
    content = content,
  )
}