package com.emendo.expensestracker.core.designsystem.component

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ExpeDropdownMenu(
  expanded: Boolean,
  items: ImmutableList<DropdownMenuItem>,
  onDismissRequest: () -> Unit = {},
) {
  val scrollState = rememberScrollState()
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    scrollState = scrollState,
    offset = DpOffset(0.dp, Dimens.margin_small_x),
  ) {
    items.forEach { item ->
      DropdownMenuItem(
        text = { Text(text = item.text) },
        onClick = {
          item.onClick()
          onDismissRequest()
        },
        leadingIcon = {
          if (item.selected) {
            Icon(
              ExpeIcons.Check,
              contentDescription = null,
            )
          }
        }
      )
    }
  }
}

@Immutable
data class DropdownMenuItem(
  val text: String,
  val selected: Boolean,
  val onClick: () -> Unit,
)