package com.emendo.expensestracker.categories.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.categories.common.CategoryContent
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectColorScreenDestination
import com.emendo.expensestracker.core.app.base.shared.destinations.SelectIconScreenDestination
import com.emendo.expensestracker.core.app.resources.R
import com.emendo.expensestracker.core.data.model.category.CategoryType
import com.emendo.expensestracker.core.ui.handleValueResult
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import de.palm.composestateevents.NavigationEventEffect

@Destination
@Composable
fun CreateCategoryRoute(
  navigator: DestinationsNavigator,
  // retrieved in ViewModel via SavedStateHandle
  @Suppress("UNUSED_PARAMETER") categoryType: CategoryType,
  colorResultRecipient: OpenResultRecipient<Int>,
  iconResultRecipient: OpenResultRecipient<Int>,
  viewModel: CreateCategoryViewModel = hiltViewModel(),
) {
  colorResultRecipient.handleValueResult(viewModel::updateColor)
  iconResultRecipient.handleValueResult(viewModel::updateIcon)

  val state = viewModel.state.collectAsStateWithLifecycle()

  NavigationEventEffect(
    event = state.value.navigateUpEvent,
    onConsumed = viewModel::consumeNavigateUpEvent,
    action = navigator::navigateUp,
  )

  CreateCategoryContent(
    stateProvider = state::value,
    onNavigationClick = navigator::navigateUp,
    onTitleChanged = remember { viewModel::changeTitle },
    onIconSelectClick = remember { { navigator.navigate(SelectIconScreenDestination(viewModel.selectedIconId)) } },
    onColorSelectClick = remember { { navigator.navigate(SelectColorScreenDestination(viewModel.selectedColorId)) } },
    onConfirmActionClick = remember { viewModel::createCategory },
  )
}

@Composable
private fun CreateCategoryContent(
  stateProvider: () -> CreateCategoryScreenData,
  onNavigationClick: () -> Unit,
  onTitleChanged: (String) -> Unit,
  onIconSelectClick: () -> Unit,
  onColorSelectClick: () -> Unit,
  onConfirmActionClick: () -> Unit,
) {
  CategoryContent(
    title = stringResource(id = R.string.create_category),
    stateProvider = stateProvider,
    onNavigationClick = onNavigationClick,
    onTitleChanged = onTitleChanged,
    onIconSelectClick = onIconSelectClick,
    onColorSelectClick = onColorSelectClick,
    onConfirmActionClick = onConfirmActionClick,
    confirmButtonText = stringResource(id = R.string.create),
    shouldFocusTitleInputOnLaunch = true,
  )
}
