package com.emendo.expensestracker.categories.list

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.bottomsheet.base.BaseScreenWithModalBottomSheetWithViewModel
import com.emendo.expensestracker.core.ui.bottomsheet.base.BottomSheetType
import com.emendo.expensestracker.core.ui.bottomsheet.calculator.CalculatorBottomSheet
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch
import com.emendo.expensestracker.core.app.resources.R as AppR

private val PADDING_CATEGORY_ITEM_PADDING = 16.dp
private const val MIN_ITEM_HEIGHT = 130
private val tabDataList = listOf(
  TabData(AppR.string.expense, CategoryType.EXPENSE),
  TabData(AppR.string.income, CategoryType.INCOME),
)
private const val GRID_CELL_COUNT = 4

@RootNavGraph(start = true)
@Destination(start = true)
@Composable
fun CategoriesListRoute(
  navigator: DestinationsNavigator,
  viewModel: CategoriesListViewModel = hiltViewModel(),
) {
  val uiState = viewModel.categoriesListUiState.collectAsStateWithLifecycle()
  val alertDialogStateFlow = viewModel.alertDialogState.collectAsStateWithLifecycle()

  BaseScreenWithModalBottomSheetWithViewModel(
    viewModel = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    content = {
      CategoriesListScreenContent(
        uiStateProvider = uiState::value,
        alertDialogStateProvider = alertDialogStateFlow::value,
        onCreateCategoryClick = remember { { navigator.navigate(CreateCategoryRouteDestination(it)) } },
        onCategoryClick = viewModel::showCalculatorBottomSheet,
        onAlertDialogDismissRequest = viewModel::handleDialogDismissRequest,
        onCloseClick = viewModel::closeDialog,
        onConfirmClick = viewModel::confirmDialog,
        onPageSelected = viewModel::pageSelected,
      )
    },
    bottomSheetContent = { type, hideBottomSheet ->
      BottomSheetContent(type = type, hideBottomSheet = hideBottomSheet)
    },
  )
}

@Composable
private fun CategoriesListScreenContent(
  uiStateProvider: () -> CategoriesListUiState,
  alertDialogStateProvider: () -> BaseDialogListUiState<CategoriesListDialogData>?,
  onCreateCategoryClick: (CategoryType) -> Unit,
  onCategoryClick: (category: CategoryWithTotalTransactions) -> Unit,
  onAlertDialogDismissRequest: () -> Unit,
  onCloseClick: () -> Unit,
  onConfirmClick: () -> Unit,
  onPageSelected: (pageIndex: Int) -> Unit,
) {
  ExpeScaffoldWithTopBar(titleResId = AppR.string.categories) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.Center,
    ) {
      when (val state = uiStateProvider()) {
        is CategoriesListUiState.Empty -> Unit
        is CategoriesListUiState.Loading -> ExpLoadingWheel()
        is CategoriesListUiState.Error -> Text(text = state.message)
        is CategoriesListUiState.DisplayCategoriesList -> CategoriesList(
          uiStateProvider = { state },
          onCreateCategoryClick = onCreateCategoryClick,
          onCategoryClick = onCategoryClick,
          onPageSelected = onPageSelected,
        )
      }
    }
  }

  Dialog(
    alertDialogStateProvider = alertDialogStateProvider,
    onAlertDialogDismissRequest = onAlertDialogDismissRequest,
    onCloseClick = onCloseClick,
    onConfirmClick = onConfirmClick,
  )
}

// Todo recomposes on transaction
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoriesList(
  uiStateProvider: () -> CategoriesListUiState.DisplayCategoriesList,
  onCreateCategoryClick: (CategoryType) -> Unit,
  onCategoryClick: (category: CategoryWithTotalTransactions) -> Unit,
  onPageSelected: (pageIndex: Int) -> Unit,
) {
  val pagerState = rememberPagerState(pageCount = { uiStateProvider().categories.size })
  val coroutineScope = rememberCoroutineScope()
  val selectedPageIndex = rememberSaveable { mutableIntStateOf(0) }

  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.currentPage }.collect { page ->
      onPageSelected(page)
      selectedPageIndex.intValue = page
    }
  }

  Column {
    TabRow(selectedTabIndex = selectedPageIndex.intValue) {
      tabDataList.forEachIndexed { index, tabData ->
        Tab(
          selected = selectedPageIndex.intValue == index,
          onClick = {
            coroutineScope.launch {
              pagerState.animateScrollToPage(index)
            }
          },
          text = {
            Text(
              text = stringResource(id = tabData.titleResId),
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
            )
          }
        )
      }
    }
    HorizontalPager(state = pagerState) { page ->
      LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(GRID_CELL_COUNT),
        verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
        horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
        contentPadding = PaddingValues(Dimens.margin_large_x),
      ) {
        items(
          items = uiStateProvider().categories[page]!!,
          key = { it.categoryModel.id },
          contentType = { "category" },
        ) {
          CategoryItem(
            name = it.categoryModel.name,
            color = it.categoryModel.color.color,
            icon = it.categoryModel.icon.imageVector,
            total = it.totalFormatted,
            onClick = { onCategoryClick(it) },
          )
        }
        uniqueItem("addCategory") {
          // Todo remove business logic
          AddCategoryItem(onClick = { onCreateCategoryClick(tabDataList[page].categoryType) })
        }
      }
    }
  }
}

@Composable
private fun CategoriesListDialog(
  showDialogProvider: State<Boolean>,
  onAlertDialogDismissRequest: () -> Unit,
  onCloseClick: () -> Unit,
  onConfirmClick: () -> Unit,
  data: () -> BaseDialogListUiState<CategoriesListDialogData>?,
) {
  if (showDialogProvider.value) {
    ExpeAlertDialog(
      onAlertDialogDismissRequest = onAlertDialogDismissRequest,
      onCloseClick = onCloseClick,
      onConfirmClick = onConfirmClick,
      title = "Select category",
    ) { AlertDialogContent(data) }
  }
}

@Composable
private fun AlertDialogContent(state: () -> BaseDialogListUiState<CategoriesListDialogData>?) {
  when (val dialogState = state()) {
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
  name: String,
  color: Color,
  icon: ImageVector,
  total: String,
  onClick: () -> Unit,
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
        text = name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
      )
      CategoryIcon(
        color = color,
        imageVector = icon,
        onClick = onClick,
      )
      Text(
        text = total,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
      )
    }
  }
}

@Composable
private fun CategoryIcon(
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
      .background(color = color)
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
      imageVector = ExpeIcons.Add,
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
private fun Dialog(
  alertDialogStateProvider: () -> BaseDialogListUiState<CategoriesListDialogData>?,
  onAlertDialogDismissRequest: () -> Unit,
  onCloseClick: () -> Unit,
  onConfirmClick: () -> Unit,
) {
  val openDialog = remember { derivedStateOf { alertDialogStateProvider() != null } }

  CategoriesListDialog(
    showDialogProvider = openDialog,
    onAlertDialogDismissRequest = onAlertDialogDismissRequest,
    onCloseClick = onCloseClick,
    onConfirmClick = onConfirmClick,
    data = alertDialogStateProvider,
  )
}

@Composable
private fun BottomSheetContent(
  type: BottomSheetType?,
  hideBottomSheet: () -> Unit,
) {
  when (type) {
    is BottomSheetType.Calculator -> {
      val state = type.state.collectAsStateWithLifecycle()

      CalculatorBottomSheet(
        textStateProvider = remember { { state.value.text } },
        currencyState = remember { { state.value.currency } },
        equalButtonState = remember { { state.value.equalButtonState } },
        decimalSeparator = type.decimalSeparator,
        source = remember { { state.value.source } },
        target = remember { { state.value.target } },
        calculatorActions = type.actions,
        numericKeyboardActions = type.numericKeyboardActions,
      )
    }

    else -> {
      Unit
    }
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
          imageVector = ExpeIcons.Remove,
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

@ExpePreview
@Composable
private fun CategoriesListPreview() {
  ExpensesTrackerTheme {
    CategoriesListScreenContent(
      uiStateProvider = {
        CategoriesListUiState.DisplayCategoriesList(
          categories = persistentMapOf(
            0 to persistentListOf(
              CategoryWithTotalTransactions(
                categoryModel = CategoryModel(
                  id = 2,
                  name = "Childcare",
                  icon = IconModel.CHILDCARE,
                  color = ColorModel.Purple,
                  type = CategoryType.EXPENSE,
                ),
                transactions = emptyList(),
                totalFormatted = "EUR 187.20",
              ),
            ),
          ),
        )
      },
      alertDialogStateProvider = { null },
      onCreateCategoryClick = {},
      onCategoryClick = {},
      onAlertDialogDismissRequest = {},
      onCloseClick = {},
      onConfirmClick = {},
      onPageSelected = {},
    )
  }
}