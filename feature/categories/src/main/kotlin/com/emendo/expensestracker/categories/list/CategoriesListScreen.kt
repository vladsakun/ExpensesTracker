package com.emendo.expensestracker.categories.list

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.destinations.CategoryDetailRouteDestination
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.core.app.common.result.IS_DEBUG_CREATE_TRANSACTION
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.utils.RoundedCornerNormalRadiusShape
import com.emendo.expensestracker.core.ui.CategoryItem
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.category.CategoriesLazyVerticalGrid
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ADD_CATEGORY_KEY = "addCategory"

// @NonSkippableComposable
@RootNavGraph(start = true)
@Destination
@Composable
fun CategoriesListRoute(
  navigator: DestinationsNavigator,
  viewModel: CategoriesListViewModel = hiltViewModel(),
) {
  val uiState = viewModel.categoriesListUiState.collectAsStateWithLifecycle()
  val editModeState = viewModel.editMode.collectAsStateWithLifecycle()

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { BottomSheetContent(it) },
  ) {
    CategoriesListScreenContent(
      stateProvider = uiState::value,
      isEditModeProvider = editModeState::value,
      onCreateCategoryClick = remember { { navigator.navigate(CreateCategoryRouteDestination(viewModel.categoryType)) } },
      onCategoryClick = remember {
        { category: CategoryModel ->
          if (viewModel.isEditMode) {
            navigator.navigate(CategoryDetailRouteDestination(category.id))
          } else {
            navigator.navigate(viewModel.getCreateTransactionScreenRoute(category))
          }
        }
      },
      onPageSelected = viewModel::pageSelected,
      onEditClick = viewModel::invertEditMode,
      onDeleteCategoryClick = viewModel::showConfirmDeleteCategoryBottomSheet,
      onMove = viewModel::onMove,
      enableEditMode = viewModel::enableEditMode,
      disableEditMode = viewModel::disableEditMode,
      openSettings = { navigator.navigate(viewModel.getSettingsRoute()) },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriesListScreenContent(
  stateProvider: () -> CategoriesListUiState,
  isEditModeProvider: () -> Boolean,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (CategoryModel) -> Unit,
  onPageSelected: (pageIndex: Int) -> Unit,
  onEditClick: () -> Unit,
  onDeleteCategoryClick: (CategoryModel) -> Unit,
  onMove: (List<CategoryModel>) -> Unit,
  enableEditMode: () -> Unit,
  disableEditMode: () -> Unit,
  openSettings: () -> Unit,
) {
  ExpeScaffold(
    topBar = {
      ExpeCenterAlignedTopBar(
        title = stringResource(id = R.string.categories),
        actions = getMenuActions(onEditClick),
        navigationIcon = {
          ExpeToolbarIcon(
            action = MenuAction(
              icon = ExpeIcons.Settings,
              onClick = openSettings,
              contentDescription = "settings",
            ),
          )
        }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onCreateCategoryClick,
        icon = { Icon(ExpeIcons.Add, null) },
        text = { Text(text = stringResource(id = R.string.categories_list_add_category_action)) },
      )
    },
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.Center,
    ) {
      when (val state = stateProvider()) {
        is CategoriesListUiState.Empty -> Unit
        is CategoriesListUiState.Loading -> ExpLoadingWheel()
        is CategoriesListUiState.Error -> Text(text = state.message)
        is CategoriesListUiState.DisplayCategoriesList -> {
          val pagerState = rememberPagerState(pageCount = { state.categories.size })
          val coroutineScope = rememberCoroutineScope()
          val selectedPageIndex = rememberSaveable { mutableIntStateOf(0) }

          LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
              onPageSelected(page)
              selectedPageIndex.intValue = page
            }
          }

          Column {
            TextSwitch(
              selectedIndex = selectedPageIndex.intValue,
              items = state.tabs.map { stringResource(id = it.titleResId) }.toImmutableList(),
              onSelectionChange = { tabIndex ->
                coroutineScope.launch {
                  pagerState.animateScrollToPage(tabIndex)
                }
              },
              modifier = Modifier.padding(horizontal = Dimens.margin_large_x),
            )
            HorizontalPager(
              state = pagerState,
              contentPadding = PaddingValues(top = Dimens.margin_large_x),
            ) { page ->
              CategoriesGrid(
                categories = state.categories[page]!!,
                editModeProvider = isEditModeProvider,
                onCategoryClick = onCategoryClick,
                onDeleteCategoryClick = onDeleteCategoryClick,
                onMove = onMove,
                onLongClick = enableEditMode,
                onClick = disableEditMode,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun getMenuActions(onEditClick: () -> Unit) = persistentListOf(
  MenuAction(
    icon = ExpeIcons.Edit,
    onClick = onEditClick,
    contentDescription = stringResource(id = R.string.edit),
  ),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoriesGrid(
  categories: CategoriesList,
  editModeProvider: () -> Boolean,
  onCategoryClick: (CategoryModel) -> Unit,
  onDeleteCategoryClick: (CategoryModel) -> Unit,
  onMove: (List<CategoryModel>) -> Unit,
  onLongClick: () -> Unit,
  onClick: () -> Unit,
) {
  LaunchedEffect(categories) {
    if (IS_DEBUG_CREATE_TRANSACTION && categories.dataList.isNotEmpty()) {
      delay(200)
      onCategoryClick(categories.dataList.first())
    }
  }
  val mutableList =
    remember(categories) {
      mutableStateListOf<CategoryModel>().apply {
        addAll(categories.dataList.toList())
      }
    }

  val gridState = rememberLazyGridState()
  val dragDropState =
    rememberGridDragDropState(
      gridState = gridState,
      key = categories,
      ignoreItem = { key -> key == ADD_CATEGORY_KEY },
    ) { fromIndex, toIndex ->
      mutableList.apply {
        add(toIndex, removeAt(fromIndex))
        onMove(mutableList)
      }
    }

  CategoriesLazyVerticalGrid(
    modifier = Modifier
      .fillMaxSize()
      .dragContainer(dragDropState, editModeProvider)
      .combinedClickable(
        onLongClick = onLongClick,
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
      ),
    state = gridState,
  ) {
    itemsIndexed(
      items = mutableList,
      key = { _, item -> item.id },
      contentType = { _, _ -> "category" },
    ) { index, category ->
      DraggableItem(dragDropState, index) { isDragging ->
        val elevation by animateDpAsState(if (isDragging) 24.dp else 0.dp, label = "drag_shadow")
        CategoryItem(
          name = category.name.stringValue(),
          color = category.color.color,
          icon = category.icon.imageVector,
          onClick = { onCategoryClick(category) },
          onDeleteClick = { onDeleteCategoryClick(category) },
          editMode = editModeProvider,
          modifier = Modifier.shadow(elevation, RoundedCornerNormalRadiusShape),
        )
      }
    }
  }
}

private val itemAnimationSpec: SpringSpec<IntOffset> = spring(
  stiffness = Spring.StiffnessLow,
  visibilityThreshold = IntOffset.VisibilityThreshold,
)

@Composable
private fun ColumnScope.BottomSheetContent(type: BottomSheetData) {
  when (type) {
    is GeneralBottomSheetData -> GeneralBottomSheet(type)
  }
}

// @NonSkippableComposable
@ExpePreview
@Composable
private fun CategoriesListPreview(
  @PreviewParameter(CategoriesListPreviewData::class) previewData: CategoriesListUiState,
) {
  ExpensesTrackerTheme {
    CategoriesListScreenContent(
      stateProvider = { previewData },
      onCreateCategoryClick = {},
      onCategoryClick = {},
      onPageSelected = {},
      onEditClick = {},
      isEditModeProvider = { false },
      onDeleteCategoryClick = {},
      onMove = {},
      enableEditMode = {},
      disableEditMode = {},
      openSettings = {},
    )
  }
}

//
// @Composable
// private fun CategoriesListDialog(
//  showDialogProvider: State<Boolean>,
//  onAlertDialogDismissRequest: () -> Unit,
//  onCloseClick: () -> Unit,
//  onConfirmClick: () -> Unit,
//  data: () -> BaseDialogListUiState<CategoriesListDialogData>?,
// ) {
//  if (showDialogProvider.value) {
//    ExpeAlertDialog(
//      onAlertDialogDismissRequest = onAlertDialogDismissRequest,
//      onCloseClick = onCloseClick,
//      onConfirmClick = onConfirmClick,
//      title = "Select category",
//    ) { AlertDialogContent(data) }
//  }
// }
//
// @Composable
// private fun AlertDialogContent(state: () -> BaseDialogListUiState<CategoriesListDialogData>?) {
//  when (val dialogState = state()) {
//    is BaseDialogListUiState.DisplayList -> {
//      when (val dialogStateData = dialogState.data) {
//        is CategoriesListDialogData.Accounts -> AccountsDialogState(dialogStateData)
//        is CategoriesListDialogData.Categories -> CategoriesDialogState(dialogStateData)
//      }
//    }
//
//    else -> Unit
//  }
// }
//
// @Composable
// private fun CategoriesDialogState(dialogStateData: CategoriesListDialogData.Categories) {
//  LazyColumn {
//    items(
//      items = dialogStateData.categories,
//      key = { it.id },
//      contentType = { "category" },
//    ) { category ->
//      Column {
//        Row(
//          horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
//          verticalAlignment = Alignment.CenterVertically,
//          modifier = Modifier
//            .fillMaxSize()
//            .clickable { dialogStateData.onSelectCategory(category) }
//            .padding(vertical = Dimens.margin_large_x, horizontal = Dimens.margin_large_xxx),
//        ) {
//          Icon(
//            imageVector = category.icon.imageVector,
//            contentDescription = "icon",
//            modifier = Modifier.size(Dimens.icon_size),
//          )
//          Text(text = category.name.stringValue())
//        }
//        ExpeDivider(modifier = Modifier.padding(horizontal = Dimens.margin_large_xxx))
//      }
//    }
//  }
// }
//
// @Composable
// private fun AccountsDialogState(dialogStateData: CategoriesListDialogData.Accounts) {
//  LazyColumn {
//    items(
//      items = dialogStateData.accountModels,
//      key = { it.id },
//      contentType = { "account" },
//    ) { account ->
//      Column {
//        Row(
//          horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
//          verticalAlignment = Alignment.CenterVertically,
//          modifier = Modifier
//            .fillMaxSize()
//            .clickable { dialogStateData.onSelectAccount(account) }
//            .padding(horizontal = Dimens.margin_large_xxx, vertical = Dimens.margin_large_x),
//        ) {
//          Icon(
//            imageVector = account.icon.imageVector,
//            contentDescription = "icon",
//            modifier = Modifier.size(Dimens.icon_size),
//          )
//          Column(verticalArrangement = Arrangement.SpaceBetween) {
//            Text(text = account.name.stringValue())
//            Spacer(modifier = Modifier.height(Dimens.margin_small_xx))
//            Text(
//              text = account.balanceFormatted,
//              style = MaterialTheme.typography.labelMedium,
//            )
//          }
//        }
//        ExpeDivider(modifier = Modifier.padding(horizontal = Dimens.margin_large_xxx))
//      }
//    }
//  }
// }
//
// @Composable
// private fun Dialog(
//  alertDialogStateProvider: () -> BaseDialogListUiState<CategoriesListDialogData>?,
//  onAlertDialogDismissRequest: () -> Unit,
//  onCloseClick: () -> Unit,
//  onConfirmClick: () -> Unit,
// ) {
//  val openDialog = remember { derivedStateOf { alertDialogStateProvider() != null } }
//
//  CategoriesListDialog(
//    showDialogProvider = openDialog,
//    onAlertDialogDismissRequest = onAlertDialogDismissRequest,
//    onCloseClick = onCloseClick,
//    onConfirmClick = onConfirmClick,
//    data = alertDialogStateProvider,
//  )
// }
