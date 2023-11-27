package com.emendo.expensestracker.createtransaction.selectcategory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.ColorModel.Companion.color
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.app.resources.models.TransactionElementName
import com.emendo.expensestracker.core.data.model.category.CategoryModel
import com.emendo.expensestracker.core.data.model.category.CategoryType
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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.emendo.expensestracker.core.app.resources.R as AppR

private const val GRID_CELL_COUNT = 3

@Destination
@Composable
fun SelectCategoryScreen(
  navigator: DestinationsNavigator,
  viewModel: SelectCategoryViewModel = hiltViewModel(),
) {
  val uiState = viewModel.selectCategoryUiState.collectAsStateWithLifecycle()

  SelectCategoryContent(
    stateProvider = uiState::value,
    onCreateCategoryClick = {
      //      navigator.navigate(CreateCategoryRouteDestination(viewModel.getCategoryType()))
    },
    onCategoryClick = { category ->
      viewModel.saveCategory(category)
      navigator.navigateUp()
    },
  )
}

@Composable
private fun SelectCategoryContent(
  stateProvider: () -> SelectCategoryUiState,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (category: CategoryModel) -> Unit,
) {
  ExpeScaffoldWithTopBar(titleResId = AppR.string.categories) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.Center,
    ) {
      when (val state = stateProvider()) {
        is SelectCategoryUiState.Empty -> Unit
        is SelectCategoryUiState.Loading -> ExpLoadingWheel()
        is SelectCategoryUiState.Error -> Text(text = state.message)
        is SelectCategoryUiState.DisplayCategoryList -> CategoriesList(
          uiStateProvider = { state },
          onCreateCategoryClick = onCreateCategoryClick,
          onCategoryClick = onCategoryClick,
        )
      }
    }
  }
}

@Composable
private fun CategoriesList(
  uiStateProvider: () -> SelectCategoryUiState.DisplayCategoryList,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (category: CategoryModel) -> Unit,
) {
  CategoriesGrid(
    categories = uiStateProvider().categories,
    onCategoryClick = onCategoryClick,
    onAddCategoryClick = onCreateCategoryClick,
  )
}

@Composable
fun CategoriesGrid(
  categories: ImmutableList<CategoryModel>,
  onCategoryClick: (category: CategoryModel) -> Unit,
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
      key = { it.id },
      contentType = { "category" },
    ) { category ->
      CategoryItem(
        name = category.name.stringValue(),
        color = category.color.color,
        icon = category.icon.imageVector,
        total = "",
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
    SelectCategoryContent(
      stateProvider = {
        SelectCategoryUiState.DisplayCategoryList(
          categories = List(6) { index ->
            CategoryModel(
              id = index.toLong(),
              name = TransactionElementName.Name("Childcare"),
              icon = IconModel.CHILDCARE,
              color = ColorModel.Purple,
              type = CategoryType.EXPENSE,
            )
          }.toImmutableList(),
        )
      },
      onCreateCategoryClick = {},
      onCategoryClick = {},
    )
  }
}