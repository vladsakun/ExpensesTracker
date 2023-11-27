package com.emendo.expensestracker.categories.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.categories.destinations.CreateCategoryRouteDestination
import com.emendo.expensestracker.categories.list.model.TabData
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TransactionElementName
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.data.model.category.CategoryWithTotalTransactions
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.Dimens
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.AddCategoryItem
import com.emendo.expensestracker.core.ui.CategoryItem
import com.emendo.expensestracker.core.ui.stringValue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import com.emendo.expensestracker.core.app.resources.R as AppR

private const val GRID_CELL_COUNT = 3

@RootNavGraph(start = true)
@Destination
@Composable
fun CategoriesListRoute(
  navigator: DestinationsNavigator,
  viewModel: CategoriesListViewModel = hiltViewModel(),
) {
  val uiState = viewModel.categoriesListUiState.collectAsStateWithLifecycle()

  CategoriesListScreenContent(
    stateProvider = uiState::value,
    onCreateCategoryClick = { navigator.navigate(CreateCategoryRouteDestination(viewModel.getCategoryType())) },
    onCategoryClick = viewModel::showCalculatorBottomSheet,
    onPageSelected = viewModel::pageSelected,
  )
}

@Composable
private fun CategoriesListScreenContent(
  stateProvider: () -> CategoriesListUiState,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (category: CategoryWithTotalTransactions) -> Unit,
  onPageSelected: (pageIndex: Int) -> Unit,
) {
  ExpeScaffoldWithTopBar(titleResId = AppR.string.categories) { paddingValues ->
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
        is CategoriesListUiState.DisplayCategoriesList -> CategoriesList(
          uiStateProvider = { state },
          onCreateCategoryClick = onCreateCategoryClick,
          onCategoryClick = onCategoryClick,
          onPageSelected = onPageSelected,
        )
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoriesList(
  uiStateProvider: () -> CategoriesListUiState.DisplayCategoriesList,
  onCreateCategoryClick: () -> Unit,
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
      uiStateProvider().tabs.forEachIndexed { index, tabData ->
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
      CategoriesGrid(
        categories = uiStateProvider().categories[page]!!,
        onCategoryClick = onCategoryClick,
        onAddCategoryClick = onCreateCategoryClick,
      )
    }
  }
}

@Composable
fun CategoriesGrid(
  categories: ImmutableList<CategoryWithTotalTransactions>,
  onCategoryClick: (category: CategoryWithTotalTransactions) -> Unit,
  onAddCategoryClick: () -> Unit,
) {
  LazyVerticalGrid(
    modifier = Modifier.fillMaxSize(),
    columns = GridCells.Fixed(GRID_CELL_COUNT),
    verticalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    horizontalArrangement = Arrangement.spacedBy(Dimens.margin_large_x),
    contentPadding = PaddingValues(Dimens.margin_large_x),
  ) {
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
      )
    }
    uniqueItem("addCategory") {
      AddCategoryItem(onClick = onAddCategoryClick)
    }
  }
}

@ExpePreview
@Composable
private fun CategoriesListPreview() {
  ExpensesTrackerTheme {
    CategoriesListScreenContent(
      stateProvider = {
        CategoriesListUiState.DisplayCategoriesList(
          categories = persistentMapOf(
            0 to List(6) { index ->
              CategoryWithTotalTransactions(
                categoryModel = CategoryModel(
                  id = index.toLong(),
                  name = TransactionElementName.Name("Childcare"),
                  icon = IconModel.CHILDCARE,
                  color = ColorModel.Purple,
                  type = CategoryType.EXPENSE,
                ),
                transactions = emptyList(),
                totalFormatted = "EUR 187.20",
              )
            }.toImmutableList(),
          ),
          tabs = persistentListOf(
            TabData(AppR.string.expense),
            TabData(AppR.string.income),
          )
        )
      },
      onCreateCategoryClick = {},
      onCategoryClick = {},
      onPageSelected = {},
    )
  }
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