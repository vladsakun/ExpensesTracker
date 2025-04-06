package com.emendo.expensestracker.createtransaction.selectcategory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.designsystem.component.ExpLoadingWheel
import com.emendo.expensestracker.core.designsystem.component.ExpePreview
import com.emendo.expensestracker.core.designsystem.component.ExpeScaffoldWithTopBar
import com.emendo.expensestracker.core.designsystem.theme.ExpensesTrackerTheme
import com.emendo.expensestracker.core.ui.CategoryItem
import com.emendo.expensestracker.core.ui.category.CategoriesLazyVerticalGrid
import com.emendo.expensestracker.core.ui.stringValue
import com.emendo.expensestracker.createtransaction.destinations.SelectCategoryScreenDestination
import com.emendo.expensestracker.data.api.model.category.CategoryModel
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.model.ui.ColorModel
import com.emendo.expensestracker.model.ui.ColorModel.Companion.color
import com.emendo.expensestracker.model.ui.textValueOf
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.scope.resultRecipient
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.emendo.expensestracker.app.resources.R as AppR

@Destination
@Composable
fun SelectCategoryScreen(
  navigator: DestinationsNavigator,
  resultNavigator: ResultBackNavigator<Long>,
  viewModel: SelectCategoryViewModel = hiltViewModel(),
) {
  val uiState = viewModel.selectCategoryUiState.collectAsStateWithLifecycle()

  SelectCategoryContent(
    stateProvider = uiState::value,
    onBackClick = navigator::navigateUp,
    onCreateCategoryClick = { navigator.navigate(viewModel.getCreateCategoryScreenRoute()) },
    onCategoryClick = { category ->
      resultNavigator.navigateBack(category.id)
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
        is SelectCategoryUiState.Empty -> Unit
        is SelectCategoryUiState.Loading -> ExpLoadingWheel()
        is SelectCategoryUiState.Error -> Text(text = state.message)
        is SelectCategoryUiState.DisplayCategoryList -> CategoriesGrid(
          categories = state.categories,
          onCategoryClick = onCategoryClick,
        )
      }
    }
  }
}

@Composable
private fun CategoriesGrid(
  categories: ImmutableList<CategoryModel>,
  onCategoryClick: (category: CategoryModel) -> Unit,
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
        onClick = { onCategoryClick(category) },
        onDeleteClick = {},
        editMode = { false },
      )
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
              subcategories = emptyList(),
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

@Composable
fun DestinationScope<*>.selectCategoryResultRecipient() =
  resultRecipient<SelectCategoryScreenDestination, Long>()