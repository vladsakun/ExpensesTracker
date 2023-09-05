package com.emendo.accounts.create.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.emendo.accounts.create.ITEM_FIXED_SIZE_DP
import com.emendo.accounts.create.SELECTED_COLOR_BORDER_WIDTH
import com.emendo.accounts.create.SELECTED_ITEM_ALPHA_BORDER
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.designsystem.component.bottomsheet.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.conditional
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ColorsBottomSheet(
  colors: ImmutableList<ColorModel>,
  selectedColor: ColorModel,
  onColorSelect: (color: ColorModel) -> Unit,
  onCloseClick: () -> Unit,
) {
  ExpeBottomSheet(
    titleResId = R.string.color,
    onCloseClick = onCloseClick,
    content = {
      Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
          columns = GridCells.FixedSize(ITEM_FIXED_SIZE_DP.dp),
          horizontalArrangement = Arrangement.SpaceAround,
        ) {
          items(
            items = colors,
            key = { item: ColorModel -> item.id },
            contentType = { _ -> "colors" },
          ) {
            ColorItem(
              color = it,
              isSelected = it == selectedColor,
              onColorSelect = onColorSelect,
            )
          }
        }
      }
    },
  )
}

@Composable
private fun ColorItem(
  color: ColorModel,
  isSelected: Boolean,
  onColorSelect: (color: ColorModel) -> Unit,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .padding(Dimens.margin_small_xx)
      .clip(shape = CircleShape)
      .clickable { onColorSelect(color) }
      .conditional(isSelected) {
        border(
          width = SELECTED_COLOR_BORDER_WIDTH.dp,
          color = color.color.copy(alpha = SELECTED_ITEM_ALPHA_BORDER),
          shape = CircleShape,
        )
      },
  ) {
    Box(
      modifier = Modifier
        .padding(Dimens.margin_large_x)
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .background(color = color.color),
    )
  }
}