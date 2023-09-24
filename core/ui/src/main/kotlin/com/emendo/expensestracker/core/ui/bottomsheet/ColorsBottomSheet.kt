package com.emendo.expensestracker.core.ui.bottomsheet

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
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.designsystem.component.ExpeBottomSheet
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.conditional
import com.emendo.expensestracker.core.ui.Constants.ITEM_FIXED_SIZE_DP
import com.emendo.expensestracker.core.ui.Constants.SELECTED_COLOR_BORDER_WIDTH
import com.emendo.expensestracker.core.ui.Constants.SELECTED_ITEM_ALPHA_BORDER
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ColorsBottomSheet(
  selectedColor: ColorModel,
  onColorSelect: (color: ColorModel) -> Unit,
  onCloseClick: () -> Unit,
  colors: ImmutableList<ColorModel> = ColorModel.entries.toImmutableList(),
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
  val colorValue = color.color
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .padding(Dimens.margin_small_xx)
      .clip(shape = CircleShape)
      .clickable { onColorSelect(color) }
      .conditional(isSelected) {
        val border = border(
          width = SELECTED_COLOR_BORDER_WIDTH.dp,
          color = colorValue.copy(alpha = SELECTED_ITEM_ALPHA_BORDER),
          shape = CircleShape,
        )
        border
      },
  ) {
    Box(
      modifier = Modifier
        .padding(Dimens.margin_large_x)
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .background(color = colorValue),
    )
  }
}