package com.emendo.expensestracker.core.ui.category

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emendo.expensestracker.core.designsystem.theme.Dimens

private const val GRID_CELL_COUNT = 3

@Composable
fun CategoriesLazyVerticalGrid(
  modifier: Modifier = Modifier,
  state: LazyGridState = rememberLazyGridState(),
  content: LazyGridScope.() -> Unit,
) {
  LazyVerticalGrid(
    modifier = modifier.fillMaxSize(),
    columns = GridCells.Fixed(GRID_CELL_COUNT),
    //    verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    //    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    contentPadding = PaddingValues(Dimens.margin_large_x),
    state = state,
    content = content,
  )
}