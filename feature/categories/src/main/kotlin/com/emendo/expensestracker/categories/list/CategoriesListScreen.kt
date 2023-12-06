package com.emendo.expensestracker.categories.list

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.categories.destinations.CategoryDetailScreenDestination
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.categories.list.drag.DraggableItem
import com.emendo.expensestracker.categories.list.drag.dragContainer
import com.emendo.expensestracker.categories.list.drag.rememberGridDragDropState
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.designsystem.component.*
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.AddCategoryItem
import com.emendo.expensestracker.core.ui.CategoryItem
import com.emendo.expensestracker.core.ui.bottomsheet.BottomSheetData
import com.emendo.expensestracker.core.ui.bottomsheet.base.ScreenWithModalBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheet
import com.emendo.expensestracker.core.ui.bottomsheet.general.GeneralBottomSheetData
import com.emendo.expensestracker.core.ui.category.CategoriesLazyVerticalGrid
import com.emendo.expensestracker.core.ui.stringValue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun CategoriesListRoute(
  navigator: DestinationsNavigator,
  viewModel: CategoriesListViewModel = hiltViewModel(),
) {
  val uiState = viewModel.categoriesListUiState.collectAsStateWithLifecycle()
  val editModeState = viewModel.editModeState.collectAsStateWithLifecycle()

  ScreenWithModalBottomSheet(
    stateManager = viewModel,
    onNavigateUpClick = navigator::navigateUp,
    bottomSheetContent = { BottomSheetContent(it) }
  ) {
    CategoriesListScreenContent(
      stateProvider = uiState::value,
      isEditModeProvider = editModeState::value,
      onCreateCategoryClick = remember { { navigator.navigate(CreateCategoryRouteDestination(viewModel.categoryType)) } },
      onCategoryClick = remember {
        { category: CategoryWithTotalTransactions ->
          if (viewModel.isEditMode) {
            navigator.navigate(CategoryDetailScreenDestination(category.categoryModel.id))
          } else {
            viewModel.openCreateTransactionScreen(category)
          }
        }
      },
      onPageSelected = remember { viewModel::pageSelected },
      onEditClick = remember { viewModel::inverseEditMode },
      onDeleteCategoryClick = remember { viewModel::showConfirmDeleteCategoryBottomSheet },
    )
  }
}

@Composable
private fun ColumnScope.BottomSheetContent(type: BottomSheetData) {
  when (type) {
    is GeneralBottomSheetData -> GeneralBottomSheet(type)
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CategoriesListScreenContent(
  stateProvider: () -> CategoriesListUiState,
  isEditModeProvider: () -> Boolean,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (CategoryWithTotalTransactions) -> Unit,
  onPageSelected: (pageIndex: Int) -> Unit,
  onEditClick: () -> Unit,
  onDeleteCategoryClick: (CategoryWithTotalTransactions) -> Unit,
) {
  ExpeScaffold(
    topBar = {
      ExpeCenterAlignedTopBar(
        title = stringResource(id = R.string.categories),
        actions = persistentListOf(
          MenuAction(
            icon = ExpeIcons.Edit,
            onClick = onEditClick,
            contentDescription = stringResource(id = R.string.edit)
          )
        )
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
            HorizontalPager(state = pagerState) { page ->
              CategoriesGrid(
                categories = state.categories[page]!!,
                isEditMode = isEditModeProvider,
                onCategoryClick = onCategoryClick,
                onCreateCategoryClick = onCreateCategoryClick,
                onDeleteCategoryClick = onDeleteCategoryClick,
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyGridTestGoogle() {
  var list by remember { mutableStateOf(List(50) { it }) }

  val gridState = rememberLazyGridState()
  val dragDropState = rememberGridDragDropState(gridState) { fromIndex, toIndex ->
    list = list.toMutableList().apply {
      add(toIndex, removeAt(fromIndex))
    }
  }

  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = Modifier.dragContainer(dragDropState),
    state = gridState,
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    itemsIndexed(list, key = { _, item -> item }) { index, item ->
      DraggableItem(dragDropState, index) { isDragging ->
        val elevation by animateDpAsState(if (isDragging) 4.dp else 1.dp)
        Card(
          modifier = Modifier
            .shadow(elevation)
        ) {
          Text(
            "Item $item",
            textAlign = TextAlign.Center,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 40.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun CategoriesGrid(
  categories: ImmutableList<CategoryWithTotalTransactions>,
  isEditMode: () -> Boolean,
  onCategoryClick: (CategoryWithTotalTransactions) -> Unit,
  onCreateCategoryClick: () -> Unit,
  onDeleteCategoryClick: (CategoryWithTotalTransactions) -> Unit,
) {
  LazyGridTestGoogle()
  //  CategoriesGridWithData(categories, onCategoryClick, isEditMode, onDeleteCategoryClick, onCreateCategoryClick)
}

@Composable
private fun CategoriesGridWithData(
  categories: ImmutableList<CategoryWithTotalTransactions>,
  onCategoryClick: (CategoryWithTotalTransactions) -> Unit,
  isEditMode: () -> Boolean,
  onDeleteCategoryClick: (CategoryWithTotalTransactions) -> Unit,
  onCreateCategoryClick: () -> Unit,
) {
  CategoriesLazyVerticalGrid {
    items(
      items = categories,
      key = { it.categoryModel.id },
      contentType = { "category" },
    ) { category ->
      CategoryItem(
        name = category.categoryModel.name.stringValue(),
        color = category.categoryModel.color.color,
        icon = category.categoryModel.icon.imageVector,
        total = category.totalFormatted,
        onClick = { onCategoryClick(category) },
        isEditMode = isEditMode,
        onDeleteClick = { onDeleteCategoryClick(category) },
      )
    }
    uniqueItem("addCategory") {
      AddCategoryItem(onClick = onCreateCategoryClick)
    }
  }
}

@ExpePreview
@Composable
private fun CategoriesListPreview() {
  //  ExpensesTrackerTheme {
  //    CategoriesListScreenContent(
  //      stateProvider = {
  //        CategoriesListUiState.DisplayCategoriesList(
  //          categories = persistentMapOf(
  //            0 to List(6) { index ->
  //              CategoryWithTotalTransactions(
  //                categoryModel = CategoryModel(
  //                  id = index.toLong(),
  //                  name = TextValue.Value("Childcare"),
  //                  icon = IconModel.CHILDCARE,
  //                  color = ColorModel.Purple,
  //                  type = CategoryType.EXPENSE,
  //                ),
  //                transactions = emptyList(),
  //                totalFormatted = "EUR 187.20",
  //              )
  //            }.toImmutableList(),
  //          ),
  //          tabs = persistentListOf(
  //            TabData(R.string.expense),
  //            TabData(R.string.income),
  //          )
  //        )
  //      },
  //      onCreateCategoryClick = {},
  //      onCategoryClick = {},
  //      onPageSelected = {},
  //      onEditClick = {},
  //    )
  //  }
}

//
//@Composable
//private fun CategoriesListDialog(
//  showDialogProvider: State<Boolean>,
//  onAlertDialogDismissRequest: () -> Unit,
//  onCloseClick: () -> Unit,
//  onConfirmClick: () -> Unit,
//  data: () -> BaseDialogListUiState<CategoriesListDialogData>?,
//) {
//  if (showDialogProvider.value) {
//    ExpeAlertDialog(
//      onAlertDialogDismissRequest = onAlertDialogDismissRequest,
//      onCloseClick = onCloseClick,
//      onConfirmClick = onConfirmClick,
//      title = "Select category",
//    ) { AlertDialogContent(data) }
//  }
//}
//
//@Composable
//private fun AlertDialogContent(state: () -> BaseDialogListUiState<CategoriesListDialogData>?) {
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
//}
//
//@Composable
//private fun CategoriesDialogState(dialogStateData: CategoriesListDialogData.Categories) {
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
//}
//
//@Composable
//private fun AccountsDialogState(dialogStateData: CategoriesListDialogData.Accounts) {
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
//}
//
//@Composable
//private fun Dialog(
//  alertDialogStateProvider: () -> BaseDialogListUiState<CategoriesListDialogData>?,
//  onAlertDialogDismissRequest: () -> Unit,
//  onCloseClick: () -> Unit,
//  onConfirmClick: () -> Unit,
//) {
//  val openDialog = remember { derivedStateOf { alertDialogStateProvider() != null } }
//
//  CategoriesListDialog(
//    showDialogProvider = openDialog,
//    onAlertDialogDismissRequest = onAlertDialogDismissRequest,
//    onCloseClick = onCloseClick,
//    onConfirmClick = onConfirmClick,
//    data = alertDialogStateProvider,
//  )
//}