package com.emendo.expensestracker.categories.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emendo.expensestracker.app.resources.R
import com.emendo.expensestracker.categories.common.CategoryContent
import com.emendo.expensestracker.categories.common.command.CategoryCommand
import com.emendo.expensestracker.categories.common.command.UpdateTitleCategoryCommand
import com.emendo.expensestracker.categories.destinations.CreateSubcategoryRouteDestination
import com.emendo.expensestracker.categories.subcategory.CreateSubcategoryResult
import com.emendo.expensestracker.core.ui.handleValueResult
import com.emendo.expensestracker.data.api.model.category.CategoryType
import com.emendo.expensestracker.model.ui.UiState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.OpenResultRecipient
import de.palm.composestateevents.NavigationEventEffect

@NonSkippableComposable
@Destination
@Composable
fun CreateCategoryRoute(
  navigator: DestinationsNavigator,
  // retrieved in ViewModel via SavedStateHandle
  @Suppress("UNUSED_PARAMETER") categoryType: CategoryType,
  colorResultRecipient: OpenResultRecipient<Int>,
  iconResultRecipient: OpenResultRecipient<Int>,
  subcategoryResultRecipient: OpenResultRecipient<CreateSubcategoryResult>,
  viewModel: CreateCategoryViewModel = hiltViewModel(),
) {
  colorResultRecipient.handleValueResult(viewModel::updateColor)
  iconResultRecipient.handleValueResult(viewModel::updateIcon)
  subcategoryResultRecipient.handleValueResult(viewModel::addSubcategory)

  val state = viewModel.state.collectAsStateWithLifecycle()

  CreateCategoryContent(
    stateProvider = state::value,
    onNavigationClick = navigator::navigateUp,
    commandProcessor = viewModel::processCommand,
    onIconSelectClick = remember { { navigator.navigate(viewModel.getSelectIconScreenRoute()) } },
    onColorSelectClick = remember { { navigator.navigate(viewModel.getSelectColorScreenRoute()) } },
    onAddSubcategoryClick = { navigator.navigate(CreateSubcategoryRouteDestination(viewModel.selectedColorId)) }
  )
}

@Composable
private fun CreateCategoryContent(
  stateProvider: () -> UiState<CategoryCreateScreenData>,
  onNavigationClick: () -> Unit,
  commandProcessor: (CategoryCommand) -> Unit,
  onIconSelectClick: () -> Unit,
  onColorSelectClick: () -> Unit,
  onAddSubcategoryClick: () -> Unit,
) {
  when (val state = stateProvider()) {
    is UiState.Data -> {
      NavigationEventEffect(
        event = state.data.navigateUpEvent,
        onConsumed = { commandProcessor(ConsumeNavigateUpEventCommand()) },
        action = onNavigationClick,
      )

      CategoryContent(
        title = stringResource(id = R.string.create_category),
        stateProvider = { state.data.categoryScreenData },
        onNavigationClick = onNavigationClick,
        onTitleChanged = { commandProcessor(UpdateTitleCategoryCommand(it)) },
        onIconSelectClick = onIconSelectClick,
        onColorSelectClick = onColorSelectClick,
        onConfirmActionClick = { commandProcessor(CreateCategoryCommand()) },
        onAddSubcategoryClick = onAddSubcategoryClick,
        confirmButtonText = stringResource(id = R.string.create),
        shouldFocusTitleInputOnLaunch = true,
      )
    }

    else -> Unit
  }
}
