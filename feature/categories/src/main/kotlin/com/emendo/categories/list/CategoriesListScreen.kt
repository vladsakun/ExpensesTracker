package com.emendo.categories.list

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.data.model.Category
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private val PADDING_CATEGORY_ITEM_PADDING = 16.dp
private const val MIN_ITEM_HEIGHT = 130

@RootNavGraph(start = true)
@Destination(start = true)
@Composable
fun CategoriesListRoute(
  navigator: DestinationsNavigator,
  viewModel: CategoriesListViewModel = hiltViewModel(),
) {
  CategoriesListScreenContent(
    uiState = viewModel.uiState.collectAsStateWithLifecycle(),
    onCreateCategoryClick = remember { { navigator.navigate(CreateCategoryRouteDestination) } }
  )
}

@Composable
private fun CategoriesListScreenContent(
  uiState: State<CategoriesListUiState>,
  onCreateCategoryClick: () -> Unit,
) {
  ExpeScaffoldWithTopBar(
    titleResId = com.emendo.expensestracker.core.app.resources.R.string.categories,
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.Center,
    ) {
      when (val state = uiState.value) {
        is CategoriesListUiState.Loading -> ExpLoadingWheel()
        is CategoriesListUiState.Error -> Text(text = state.message)
        is CategoriesListUiState.DisplayCategoriesList -> CategoriesList(state, onCreateCategoryClick)
      }
    }
  }
}

@Composable
private fun CategoriesList(
  uiState: CategoriesListUiState.DisplayCategoriesList,
  onCreateCategoryClick: () -> Unit,
) {
  LazyVerticalGrid(
    modifier = Modifier.fillMaxSize(),
    columns = GridCells.Fixed(4),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    contentPadding = PaddingValues(Dimens.margin_large_x),
  ) {
    items(
      items = uiState.categories,
      key = { it.id },
      contentType = { "category" },
    ) { CategoryItem(it) }
    uniqueItem("addCategory") {
      AddCategoryItem(onClick = onCreateCategoryClick)
    }
  }
}

@Composable
private fun CategoryItem(category: Category) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = MIN_ITEM_HEIGHT.dp)
      .padding(Dimens.margin_small_x),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(Dimens.margin_small_x),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = category.name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelMedium,
      )
      ColoredBorderIcon(
        color = category.color.color,
        imageVector = category.icon.imageVector,
        onClick = {},
      )
      Text(
        text = "$ 8,485",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
      )
    }
  }
}

@Composable
private fun ColoredBorderIcon(
  color: Color,
  imageVector: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Icon(
    imageVector = imageVector,
    contentDescription = "icon",
    modifier = modifier
      .clip(CircleShape)
      .aspectRatio(1f)
      .clickable(onClick = onClick)
      .background(color = color.copy(alpha = 0.15f))
      .border(
        width = Dimens.border_thickness,
        color = color,
        shape = CircleShape,
      )
      .padding(Dimens.margin_large_x),
  )
}

@Composable
private fun AddCategoryItem(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  // Todo do custom ripple effect animation
  Box(
    modifier = Modifier
      .heightIn(min = MIN_ITEM_HEIGHT.dp)
      .padding(Dimens.margin_small_x),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = ExpIcons.Add,
      contentDescription = "icon",
      modifier = modifier
        .clip(CircleShape)
        .aspectRatio(1f)
        .clickable(onClick = onClick)
        .background(color = MaterialTheme.colorScheme.primaryContainer)
        .padding(Dimens.margin_large_x),
    )
  }
}

@Composable
fun RoundedRectangleItem(
  cellSizeDp: Dp,
  icon: ImageVector,
  backgroundColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {},
  text: String = "",
  isEditMode: Boolean,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
  val rotation by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "rotation"
  )

  Column(
    modifier = modifier
      .width(cellSizeDp)
      .wrapContentHeight()
      .padding(PADDING_CATEGORY_ITEM_PADDING)
      .graphicsLayer {
        if (isEditMode) {
          rotationZ = rotation
        }
      },
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box {
      if (isEditMode) {
        Icon(
          imageVector = ExpIcons.Remove,
          contentDescription = "remove",
          modifier = Modifier
            .size(16.dp)
            .zIndex(2f)
            .background(Color.Gray, CircleShape)
            .align(Alignment.TopStart)
        )
      }

    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = text,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.labelSmall
    )
  }
}