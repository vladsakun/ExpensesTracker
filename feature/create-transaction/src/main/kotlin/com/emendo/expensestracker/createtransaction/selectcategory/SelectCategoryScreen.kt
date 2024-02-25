package com.emendo.expensestracker.createtransaction.selectcategory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.emendo.expensestracker.core.app.resources.models.textValueOf
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.designsystem.utils.uniqueItem
import com.emendo.expensestracker.core.ui.AddCategoryItem
import com.emendo.expensestracker.core.ui.CategoryItem
import com.emendo.expensestracker.core.ui.category.CategoriesLazyVerticalGrid
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.emendo.expensestracker.core.app.resources.R as AppR

@Destination
@Composable
internal fun SelectCategoryScreen(
  navigator: DestinationsNavigator,
  viewModel: SelectCategoryViewModel = hiltViewModel(),
) {
  val uiState = viewModel.selectCategoryUiState.collectAsStateWithLifecycle()

  SelectCategoryContent(
    stateProvider = uiState::value,
    onBackClick = navigator::navigateUp,
    onCreateCategoryClick = viewModel::openCreateCategoryScreen,
    onCategoryClick = { category ->
      viewModel.saveCategory(category)
      navigator.navigateUp()
    },
  )
}

@Composable
private fun SelectCategoryContent(
  stateProvider: () -> SelectCategoryUiState,
  onBackClick: () -> Unit,
  onCreateCategoryClick: () -> Unit,
  onCategoryClick: (category: CategoryModel) -> Unit,
) {
  ExpeScaffoldWithTopBar(
    titleResId = AppR.string.categories,
    onNavigationClick = onBackClick,
  ) { paddingValues ->
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
        is SelectCategoryUiState.DisplayCategoryList -> CategoriesGrid(
          categories = state.categories,
          onCategoryClick = onCategoryClick,
          onAddCategoryClick = onCreateCategoryClick,
        )
      }
    }
  }
}

@Composable
fun CategoriesGrid(
  categories: ImmutableList<CategoryModel>,
  onCategoryClick: (category: CategoryModel) -> Unit,
  onAddCategoryClick: () -> Unit,
) {
  CategoriesLazyVerticalGrid(
    modifier = Modifier.fillMaxSize()
  ) {
    items(
      items = categories,
      key = CategoryModel::id,
      contentType = { "category" },
    ) { category ->
      CategoryItem(
        name = category.name.stringValue(),
        color = category.color.color,
        icon = category.icon.imageVector,
        total = "",
        onClick = { onCategoryClick(category) },
        isEditMode = { false },
        onDeleteClick = {},
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
              name = textValueOf("Childcare"),
              icon = IconModel.random,
              color = ColorModel.random,
              type = CategoryType.EXPENSE,
              ordinalIndex = index,
              currency = null,
            )
          }.toImmutableList(),
        )
      },
      onBackClick = {},
      onCreateCategoryClick = {},
      onCategoryClick = {},
    )
  }
}