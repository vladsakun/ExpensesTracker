package com.emendo.expensestracker.createtransaction.transaction.design.additional

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.emendo.expensestracker.core.designsystem.component.ExpeButtonWithIcon
import com.emendo.expensestracker.core.designsystem.theme.Dimens

@Composable
internal fun RowScope.AdditionalAction(
  @StringRes titleResId: Int,
  icon: ImageVector,
  onClick: () -> Unit,
  enabled: Boolean = true,
) {
  ExpeButtonWithIcon(
    titleResId = titleResId,
    icon = icon,
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier
      .weight(1f)
      .heightIn(min = Dimens.icon_button_size),
  )
}