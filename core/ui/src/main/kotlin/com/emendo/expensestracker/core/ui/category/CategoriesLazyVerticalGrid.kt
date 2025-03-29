package com.emendo.expensestracker.core.ui.category

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.core.designsystem.theme.Dimens

private const val GRID_CELL_COUNT = 4

@Composable
fun CategoriesLazyVerticalGrid(
  modifier: Modifier = Modifier,
  state: LazyGridState = rememberLazyGridState(),
  content: LazyGridScope.() -> Unit,
) {
  LazyVerticalGrid(
    modifier = modifier,
    columns = GridCells.Fixed(GRID_CELL_COUNT),
    contentPadding = PaddingValues(
      start = Dimens.margin_small_x,
      end = Dimens.margin_small_x,
      bottom = Dimens.margin_small_x + Dimens.FloatingActionButtonHeight,
    ),
    state = state,
    content = content,
  )
}