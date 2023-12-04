package com.emendo.expensestracker.core.ui.bottomsheet

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.conditional
import com.emendo.expensestracker.core.ui.Constants.ITEM_FIXED_SIZE_DP
import com.emendo.expensestracker.core.ui.Constants.SELECTED_ICON_BORDER_WIDTH
import com.emendo.expensestracker.core.ui.Constants.SELECTED_ITEM_ALPHA_BORDER
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun IconsBottomSheet(
  selectedIcon: IconModel,
  onIconSelect: (color: IconModel) -> Unit,
  onCloseClick: () -> Unit,
  icons: ImmutableList<IconModel> = IconModel.entries.toImmutableList(),
) {
  ExpeBottomSheet(
    titleResId = R.string.icon,
    onCloseClick = onCloseClick,
    content = {
      Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
          columns = GridCells.FixedSize(ITEM_FIXED_SIZE_DP.dp),
          horizontalArrangement = Arrangement.SpaceAround,
        ) {
          items(
            items = icons,
            key = IconModel::id,
            contentType = { _ -> "icons" },
          ) { item ->
            IconItem(
              icon = item,
              isSelected = item == selectedIcon,
              onIconSelect = onIconSelect,
            )
          }
        }
      }
    },
  )
}

@Composable
private fun IconItem(
  icon: IconModel,
  isSelected: Boolean,
  onIconSelect: (icon: IconModel) -> Unit,
) {
  val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = SELECTED_ITEM_ALPHA_BORDER)

  Surface {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .aspectRatio(1f)
        .padding(Dimens.margin_small_xx)
        .clip(CircleShape)
        .clickable { onIconSelect(icon) }
        .conditional(isSelected) {
          border(
            width = SELECTED_ICON_BORDER_WIDTH.dp,
            color = borderColor,
            shape = CircleShape,
          )
        }
    ) {
      Icon(
        imageVector = icon.imageVector,
        contentDescription = icon.name,
      )
    }
  }
}