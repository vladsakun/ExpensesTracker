package com.emendo.categories.list

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.categories.destinations.CreateCategoryScreenDestination
import com.emendo.expensestracker.core.app.resources.icon.ExpIcons
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.feature.categories.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private val PADDING_CATEGORY_ITEM_PADDING = 16.dp
private const val OVERVIEW_BLOCK_CATEGORIES_AMOUNT = 8

inline val CategoryItemType.key
  get() = if (this is CategoryItemType.CategoryItem) category.id else -1

@Destination(start = true)
@RootNavGraph(start = true)
@Composable
fun CategoriesListScreen(
  navigator: DestinationsNavigator,
  viewModel: CategoriesListViewModel = hiltViewModel()
) {
  var isEditMode by remember { mutableStateOf(false) }

  viewModel.registerListener()

  LaunchedEffect(true) {
    viewModel.navigationEvent.collect {
      if (it != null) {
        isEditMode = !isEditMode
        //        navigator.navigate(CreateCategoryScreenDestination)
      }
    }
  }

  val categories by viewModel.uiState.collectAsStateWithLifecycle()

  val cellSizeDp = (LocalConfiguration.current.screenWidthDp / 4).dp

  CategoriesListScreenContent(
    categories,
    cellSizeDp,
    isEditMode
  ) {
    navigator.navigate(CreateCategoryScreenDestination)
  }
}

@Composable
private fun CategoriesListScreenContent(
  uiState: CategoriesListUiState,
  cellSizeDp: Dp,
  isEditMode: Boolean,
  onClick: () -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    when (uiState) {
      is CategoriesListUiState.Loading -> item {
        ExpLoadingWheel(
          contentDesc = stringResource(id = R.string.categories_loading)
        )
      }

      is CategoriesListUiState.Error -> item {
        Text(text = uiState.message)
      }

      is CategoriesListUiState.DisplayCategoriesList -> {
        val categories = uiState.categories
        val categoriesAfterOverviewAmount = categories.size - OVERVIEW_BLOCK_CATEGORIES_AMOUNT

        item(key = 0) {
          Row(
            modifier = Modifier.fillMaxWidth()
          ) {
            categories.take(4).forEach {
              CategoryScreenItem(
                categoryItemType = it,
                cellSizeDp = cellSizeDp,
                onClick = onClick,
                isEditMode = isEditMode
              )
            }
          }
        }

        item(key = 1) {
          Row(
            modifier = Modifier
              .width(cellSizeDp * 4)
              .wrapContentHeight()
          ) {
            Column(
              modifier = Modifier
                .width(cellSizeDp)
                .wrapContentHeight(),
              verticalArrangement = Arrangement.Top
            ) {
              categories.getOrNull(4)?.let {
                CategoryScreenItem(it, cellSizeDp, onClick, isEditMode)
              }
              categories.getOrNull(6)?.let {
                CategoryScreenItem(it, cellSizeDp, onClick, isEditMode)
              }
            }
            Box(
              modifier = Modifier
                .width(cellSizeDp * 2)
                .height(cellSizeDp * 2)
                .padding(PADDING_CATEGORY_ITEM_PADDING)
                .padding(top = PADDING_CATEGORY_ITEM_PADDING)
                .background(Color.Blue)
                .aspectRatio(1f)
            )
            Column(
              modifier = Modifier
                .width(cellSizeDp)
                .wrapContentHeight(),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              categories.getOrNull(5)?.let {
                CategoryScreenItem(it, cellSizeDp, onClick, isEditMode)
              }
              categories.getOrNull(7)?.let {
                CategoryScreenItem(it, cellSizeDp, onClick, isEditMode)
              }
            }
          }
        }

        if (categoriesAfterOverviewAmount > 0) {
          categories.takeLast(categoriesAfterOverviewAmount).chunked(4).forEach { categories ->
            item(key = categories.first().key) {
              Row(modifier = Modifier.fillMaxWidth()) {
                categories.forEach {
                  CategoryScreenItem(it, cellSizeDp, onClick, isEditMode)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryScreenItem(
  categoryItemType: CategoryItemType,
  cellSizeDp: Dp,
  onClick: () -> Unit,
  isEditMode: Boolean,
) {
  when (categoryItemType) {
    is CategoryItemType.AddCategoryItemType ->
      AddCategoryItem(
        cellSizeDp = cellSizeDp,
        onClick = onClick,
      )

    is CategoryItemType.CategoryItem -> {
      CategoryItem(
        category = categoryItemType,
        cellSizeDp = cellSizeDp,
        isEditMode = isEditMode,
      )
    }
  }
}

@Composable
fun CategoryItem(
  category: CategoryItemType.CategoryItem,
  cellSizeDp: Dp,
  isEditMode: Boolean,
) {
  RoundedRectangleItem(
    cellSizeDp = cellSizeDp,
    icon = category.category.icon.imageVector,
    backgroundColor = Color.Red,
    text = category.category.name,
    isEditMode = isEditMode,
  )
}

@Composable
fun AddCategoryItem(
  cellSizeDp: Dp,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  RoundedRectangleItem(
    cellSizeDp = cellSizeDp,
    icon = ExpIcons.Add,
    backgroundColor = Color.Green,
    modifier = modifier,
    onClick = onClick,
    isEditMode = false
  )
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
  val infiniteTransition = rememberInfiniteTransition()
  val rotation by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    )
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
        Image(
          imageVector = ExpIcons.Remove,
          contentDescription = "remove",
          modifier = Modifier
            .size(16.dp)
            .zIndex(2f)
            .background(Color.Gray, CircleShape)
            .align(Alignment.TopStart)
        )
      }
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(4.dp)
          .aspectRatio(1f)
          .clip(RoundedCornerShape(16.dp))
          .background(backgroundColor, RectangleShape)
          .clickable(onClick = onClick)
      ) {
        Image(
          imageVector = icon,
          contentDescription = "category icon",
          modifier = Modifier
            .fillMaxSize(0.6f)
            .align(Alignment.Center)
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