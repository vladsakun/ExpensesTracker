package com.emendo.categories.list

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.emendo.expensestracker.core.data.model.CategoryWithTransactions
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpeAlertDialog
import com.emendo.expensestracker.core.designsystem.component.ExpeDivider
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseScreenWithModalBottomSheetWithViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.calculator.CalculatorBS
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.StateFlow

private val PADDING_CATEGORY_ITEM_PADDING = 16.dp
private const val MIN_ITEM_HEIGHT = 130

@RootNavGraph(start = true)
@Destination(start = true)
@Composable
fun CategoriesListRoute(
  navigator: DestinationsNavigator,
  viewModel: CategoriesListViewModel = hiltViewModel(),
) {
  BaseScreenWithModalBottomSheetWithViewModel(
    viewModel = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    content = {
      CategoriesListScreenContent(
        uiStateFlow = viewModel.uiState,
        alertDialogStateFlow = viewModel.alertDialogState,
        onCreateCategoryClick = remember { { navigator.navigate(CreateCategoryRouteDestination) } },
        onCategoryClick = viewModel::onCategoryClick,
        onAlertDialogDismissRequest = viewModel::onAlertDialogDismissRequest,
        onCloseClick = viewModel::onCloseClick,
        onConfirmClick = viewModel::onConfirmClick,
      )
    },
    bottomSheetContent = { type, hideBottomSheet ->
      BottomSheetContent(type = type, hideBottomSheet = hideBottomSheet)
    },
  )
}

@Composable
private fun CategoriesListScreenContent(
  uiStateFlow: StateFlow<CategoriesListUiState>,
  alertDialogStateFlow: StateFlow<BaseDialogListUiState<CategoriesListDialogData>?>,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (category: CategoryWithTransactions) -> Unit,
  onAlertDialogDismissRequest: () -> Unit,
  onCloseClick: () -> Unit,
  onConfirmClick: () -> Unit,
) {
  val uiState = uiStateFlow.collectAsStateWithLifecycle()
  val alertDialogState = alertDialogStateFlow.collectAsStateWithLifecycle()
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
        is CategoriesListUiState.DisplayCategoriesList -> CategoriesList(
          uiState = state,
          onCreateCategoryClick = onCreateCategoryClick,
          onCategoryClick = onCategoryClick,
        )
      }
    }
  }

  Dialog(
    alertDialogState = alertDialogState,
    onAlertDialogDismissRequest = onAlertDialogDismissRequest,
    onCloseClick = onCloseClick,
    onConfirmClick = onConfirmClick
  )
}

@Composable
private fun Dialog(
  alertDialogState: State<BaseDialogListUiState<CategoriesListDialogData>?>,
  onAlertDialogDismissRequest: () -> Unit,
  onCloseClick: () -> Unit,
  onConfirmClick: () -> Unit,
) {
  val openDialog = remember { derivedStateOf { alertDialogState.value != null } }

  CategoriesListDialog(
    openDialog = openDialog,
    onAlertDialogDismissRequest = onAlertDialogDismissRequest,
    onCloseClick = onCloseClick,
    onConfirmClick = onConfirmClick,
    data = alertDialogState
  )
}

@Composable
private fun CategoriesList(
  uiState: CategoriesListUiState.DisplayCategoriesList,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (category: CategoryWithTransactions) -> Unit,
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
      key = { it.category.id },
      contentType = { "category" },
    ) {
      CategoryItem(
        category = it,
        onClick = onCategoryClick
      )
    }
    uniqueItem("addCategory") {
      AddCategoryItem(onClick = onCreateCategoryClick)
    }
  }
}

@Composable
private fun CategoriesListDialog(
  openDialog: State<Boolean>,
  onAlertDialogDismissRequest: () -> Unit,
  onCloseClick: () -> Unit,
  onConfirmClick: () -> Unit,
  data: State<BaseDialogListUiState<CategoriesListDialogData>?>,
) {
  if (openDialog.value) {
    ExpeAlertDialog(
      onAlertDialogDismissRequest = onAlertDialogDismissRequest,
      onCloseClick = onCloseClick,
      onConfirmClick = onConfirmClick
    ) { AlertDialogContent(data) }
  }
}

@Composable
private fun AlertDialogContent(state: State<BaseDialogListUiState<CategoriesListDialogData>?>) {
  when (val dialogState = state.value) {
    is BaseDialogListUiState.DisplayList -> {
      when (val dialogStateData = dialogState.data) {
        is CategoriesListDialogData.Accounts -> AccountsDialogState(dialogStateData)
        is CategoriesListDialogData.Categories -> CategoriesDialogState(dialogStateData)
      }
    }

    else -> Unit
  }
}

@Composable
private fun CategoriesDialogState(dialogStateData: CategoriesListDialogData.Categories) {
  LazyColumn {
    items(
      items = dialogStateData.categories,
      key = { it.id },
      contentType = { "category" },
    ) { category ->
      Column {
        Row(
          horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxSize()
            .clickable { dialogStateData.onSelectCategory(category) }
            .padding(vertical = Dimens.margin_large_x, horizontal = Dimens.margin_large_xxx),
        ) {
          Icon(
            imageVector = category.icon.imageVector,
            contentDescription = "icon",
            modifier = Modifier.size(Dimens.icon_size),
          )
          Text(text = category.name)
        }
        ExpeDivider(modifier = Modifier.padding(horizontal = Dimens.margin_large_xxx))
      }
    }
  }
}

@Composable
private fun AccountsDialogState(dialogStateData: CategoriesListDialogData.Accounts) {
  LazyColumn {
    items(
      items = dialogStateData.accountModels,
      key = { it.id },
      contentType = { "account" },
    ) { account ->
      Column {
        Row(
          horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxSize()
            .clickable { dialogStateData.onSelectAccount(account) }
            .padding(horizontal = Dimens.margin_large_xxx, vertical = Dimens.margin_large_x),
        ) {
          Icon(
            imageVector = account.icon.imageVector,
            contentDescription = "icon",
            modifier = Modifier.size(Dimens.icon_size),
          )
          Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(text = account.name)
            Spacer(modifier = Modifier.height(Dimens.margin_small_xx))
            Text(
              text = account.balanceFormatted,
              style = MaterialTheme.typography.labelMedium,
            )
          }
        }
        ExpeDivider(modifier = Modifier.padding(horizontal = Dimens.margin_large_xxx))
      }
    }
  }
}

@Composable
private fun CategoryItem(
  category: CategoryWithTransactions,
  onClick: (category: CategoryWithTransactions) -> Unit,
) {
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
        text = category.category.name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
      )
      ColoredBorderIcon(
        color = category.category.color.color,
        imageVector = category.category.icon.imageVector,
        onClick = { onClick(category) },
      )
      Text(
        text = category.totalFormatted,
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
    tint = color,
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
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is BottomSheetType.Calculator -> CalculatorBS(
      textState = type.text.collectAsStateWithLifecycle(),
      currencyState = type.currencyState.collectAsStateWithLifecycle(),
      equalButtonState = type.equalButtonState.collectAsStateWithLifecycle(),
      actions = type.actions,
      decimalSeparator = type.decimalSeparator,
      source = type.source.collectAsStateWithLifecycle(),
      target = type.target.collectAsStateWithLifecycle(),
    )

    else -> Unit
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